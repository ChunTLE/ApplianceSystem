package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.Product;
import cn.pcs.appliancesystem.entity.Sale;
import cn.pcs.appliancesystem.entity.SaleRecordVO;
import cn.pcs.appliancesystem.entity.SysUser;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.ProductMapper;
import cn.pcs.appliancesystem.mapper.SaleMapper;
import cn.pcs.appliancesystem.mapper.SysUserMapper;
import cn.pcs.appliancesystem.service.ProductService;
import cn.pcs.appliancesystem.service.SaleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;
    private final ProductMapper productMapper;
    private final SysUserMapper sysUserMapper;
    private final ProductService productService;

    @Override
    @Transactional
    public void sell(Long productId, Integer quantity, Long salesmanId) {
        // 1. 参数校验
        if (productId == null) {
            throw new BusinessException("产品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("销售数量必须大于0");
        }
        if (salesmanId == null) {
            throw new BusinessException("销售员ID不能为空");
        }

        // 2. 查询产品
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }

        // 3. 减库存
        productService.decreaseStock(productId, quantity);

        // 4. 计算总价
        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        // 5. 写销售记录
        Sale sale = Sale.builder()
                .productId(productId)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .salesmanId(salesmanId)
                .saleTime(LocalDateTime.now())
                .build();
        saleMapper.insert(sale);
    }

    @Override
    public List<SaleRecordVO> getSaleRecords() {
        // 查询所有销售记录
        List<Sale> sales = saleMapper.selectList(new LambdaQueryWrapper<>());
        
        // 提取所有涉及的产品ID和销售员ID
        List<Long> productIds = sales.stream()
                .map(Sale::getProductId)
                .distinct()
                .collect(Collectors.toList());
                
        List<Long> salesmanIds = sales.stream()
                .map(Sale::getSalesmanId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品和销售员信息
        Map<Long, String> productNamesMap = productIds.isEmpty() ? 
                Map.of() : 
                productService.listAll().stream()
                        .filter(p -> productIds.contains(p.getId()))
                        .collect(Collectors.toMap(Product::getId, Product::getProductName));
                        
        Map<Long, String> salesmanNamesMap = salesmanIds.isEmpty() ? 
                Map.of() : 
                sysUserMapper.selectBatchIds(salesmanIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));
        
        // 组装返回数据
        return sales.stream()
                .map(sale -> SaleRecordVO.builder()
                        .id(sale.getId())
                        .productName(productNamesMap.getOrDefault(sale.getProductId(), "未知产品"))
                        .quantity(sale.getQuantity())
                        .totalPrice(sale.getTotalPrice())
                        .salesman(salesmanNamesMap.getOrDefault(sale.getSalesmanId(), "未知销售员"))
                        .saleTime(sale.getSaleTime())
                        .build())
                .sorted((a, b) -> b.getSaleTime().compareTo(a.getSaleTime())) // 按时间倒序排列
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSale(Long id, Integer quantity) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("销售记录ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("销售数量必须大于0");
        }
        
        // 查询原记录
        Sale originalSale = saleMapper.selectById(id);
        if (originalSale == null) {
            throw new BusinessException("销售记录不存在");
        }
        
        // 获取产品信息以计算新的总价
        Product product = productMapper.selectById(originalSale.getProductId());
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 计算库存变化量
        int diff = quantity - originalSale.getQuantity();
        
        // 更新库存
        if (diff > 0) {
            productService.decreaseStock(originalSale.getProductId(), diff);
        } else if (diff < 0) {
            productService.increaseStock(originalSale.getProductId(), Math.abs(diff));
        }
        
        // 计算新的总价
        BigDecimal newTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        
        // 更新销售记录
        originalSale.setQuantity(quantity);
        originalSale.setTotalPrice(newTotalPrice);
        saleMapper.updateById(originalSale);
    }

    @Override
    @Transactional
    public void deleteSale(Long id) {
        if (id == null) {
            throw new BusinessException("销售记录ID不能为空");
        }
        
        Sale sale = saleMapper.selectById(id);
        if (sale == null) {
            throw new BusinessException("销售记录不存在");
        }
        
        // 增加库存（因为删除销售记录相当于把销售的商品还回去）
        productService.increaseStock(sale.getProductId(), sale.getQuantity());
        
        // 删除销售记录
        saleMapper.deleteById(id);
    }
}