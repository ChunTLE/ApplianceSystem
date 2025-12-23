package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.Product;
import cn.pcs.appliancesystem.entity.Sale;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.ProductMapper;
import cn.pcs.appliancesystem.mapper.SaleMapper;
import cn.pcs.appliancesystem.service.ProductService;
import cn.pcs.appliancesystem.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;
    private final ProductMapper productMapper;
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
}
