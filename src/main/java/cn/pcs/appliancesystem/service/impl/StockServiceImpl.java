package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.StockIn;
import cn.pcs.appliancesystem.entity.StockOut;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.StockInMapper;
import cn.pcs.appliancesystem.mapper.StockOutMapper;
import cn.pcs.appliancesystem.service.ProductService;
import cn.pcs.appliancesystem.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockInMapper stockInMapper;
    private final StockOutMapper stockOutMapper;
    private final ProductService productService;

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
}
