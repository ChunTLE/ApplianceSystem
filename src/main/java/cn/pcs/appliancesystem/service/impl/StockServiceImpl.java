package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.*;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.StockInMapper;
import cn.pcs.appliancesystem.mapper.StockOutMapper;
import cn.pcs.appliancesystem.mapper.SysUserMapper;
import cn.pcs.appliancesystem.service.ProductService;
import cn.pcs.appliancesystem.service.StockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockInMapper stockInMapper;
    private final StockOutMapper stockOutMapper;
    private final ProductService productService;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void stockIn(Long productId, Integer quantity, Long operatorId) {
        // 1. 参数校验
        if (productId == null) {
            throw new BusinessException("产品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("入库数量必须大于0");
        }
        if (operatorId == null) {
            throw new BusinessException("操作员ID不能为空");
        }

        // 2. 写入入库记录
        StockIn stockIn = StockIn.builder()
                .productId(productId)
                .quantity(quantity)
                .operatorId(operatorId)
                .inTime(LocalDateTime.now())
                .build();
        stockInMapper.insert(stockIn);

        // 3. 增加库存
        productService.increaseStock(productId, quantity);
    }

    @Override
    @Transactional
    public void stockOut(Long productId, Integer quantity, Long operatorId) {
        // 1. 参数校验
        if (productId == null) {
            throw new BusinessException("产品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("出库数量必须大于0");
        }
        if (operatorId == null) {
            throw new BusinessException("操作员ID不能为空");
        }

        // 2. 减少库存（校验在里面）
        productService.decreaseStock(productId, quantity);

        // 3. 写出库记录
        StockOut stockOut = StockOut.builder()
                .productId(productId)
                .quantity(quantity)
                .operatorId(operatorId)
                .outTime(LocalDateTime.now())
                .build();
        stockOutMapper.insert(stockOut);
    }

    @Override
    public List<StockInRecordVO> getStockInRecords() {
        // 查询所有入库记录
        List<StockIn> stockIns = stockInMapper.selectList(new LambdaQueryWrapper<>());
        
        // 提取所有涉及的产品ID和操作员ID
        List<Long> productIds = stockIns.stream()
                .map(StockIn::getProductId)
                .distinct()
                .collect(Collectors.toList());
                
        List<Long> operatorIds = stockIns.stream()
                .map(StockIn::getOperatorId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品和操作员信息
        Map<Long, String> productNamesMap = productIds.isEmpty() ? 
                Map.of() : 
                productService.listAll().stream()
                        .filter(p -> productIds.contains(p.getId()))
                        .collect(Collectors.toMap(Product::getId, Product::getProductName));
                        
        Map<Long, String> operatorNamesMap = operatorIds.isEmpty() ? 
                Map.of() : 
                sysUserMapper.selectBatchIds(operatorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));
        
        // 组装返回数据
        return stockIns.stream()
                .map(stockIn -> StockInRecordVO.builder()
                        .id(stockIn.getId())
                        .productName(productNamesMap.getOrDefault(stockIn.getProductId(), "未知产品"))
                        .quantity(stockIn.getQuantity())
                        .operator(operatorNamesMap.getOrDefault(stockIn.getOperatorId(), "未知操作员"))
                        .inTime(stockIn.getInTime())
                        .build())
                .sorted((a, b) -> b.getInTime().compareTo(a.getInTime())) // 按时间倒序排列
                .collect(Collectors.toList());
    }

    @Override
    public List<StockOutRecordVO> getStockOutRecords() {
        // 查询所有出库记录
        List<StockOut> stockOuts = stockOutMapper.selectList(new LambdaQueryWrapper<>());
        
        // 提取所有涉及的产品ID和操作员ID
        List<Long> productIds = stockOuts.stream()
                .map(StockOut::getProductId)
                .distinct()
                .collect(Collectors.toList());
                
        List<Long> operatorIds = stockOuts.stream()
                .map(StockOut::getOperatorId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品和操作员信息
        Map<Long, String> productNamesMap = productIds.isEmpty() ? 
                Map.of() : 
                productService.listAll().stream()
                        .filter(p -> productIds.contains(p.getId()))
                        .collect(Collectors.toMap(Product::getId, Product::getProductName));
                        
        Map<Long, String> operatorNamesMap = operatorIds.isEmpty() ? 
                Map.of() : 
                sysUserMapper.selectBatchIds(operatorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));
        
        // 组装返回数据
        return stockOuts.stream()
                .map(stockOut -> StockOutRecordVO.builder()
                        .id(stockOut.getId())
                        .productName(productNamesMap.getOrDefault(stockOut.getProductId(), "未知产品"))
                        .quantity(stockOut.getQuantity())
                        .operator(operatorNamesMap.getOrDefault(stockOut.getOperatorId(), "未知操作员"))
                        .outTime(stockOut.getOutTime())
                        .build())
                .sorted((a, b) -> b.getOutTime().compareTo(a.getOutTime())) // 按时间倒序排列
                .collect(Collectors.toList());
    }
}