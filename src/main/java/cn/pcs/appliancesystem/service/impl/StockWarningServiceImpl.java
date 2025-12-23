package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.Product;
import cn.pcs.appliancesystem.entity.StockWarning;
import cn.pcs.appliancesystem.mapper.ProductMapper;
import cn.pcs.appliancesystem.service.StockWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockWarningServiceImpl implements StockWarningService {
    
    private final ProductMapper productMapper;
    private static final int DEFAULT_THRESHOLD = 10;
    
    @Override
    public List<StockWarning> getWarningList(Integer threshold) {
        if (threshold == null || threshold < 0) {
            threshold = DEFAULT_THRESHOLD;
        }
        
        List<Product> products = productMapper.selectList(null);
        List<StockWarning> warnings = new ArrayList<>();
        
        for (Product product : products) {
            if (product.getStock() == null) {
                continue;
            }
            
            if (product.getStock() <= threshold) {
                int level = product.getStock() == 0 ? 2 : 1; // 2-缺货，1-低库存
                warnings.add(StockWarning.builder()
                        .productId(product.getId())
                        .productName(product.getProductName())
                        .stock(product.getStock())
                        .threshold(threshold)
                        .level(level)
                        .build());
            }
        }
        
        return warnings;
    }
}

