package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.Product;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.ProductMapper;
import cn.pcs.appliancesystem.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public List<Product> listAll() {
        return productMapper.selectList(null);
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }
    
    @Override
    public List<Product> search(String productName, Long typeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.trim().isEmpty()) {
            wrapper.like(Product::getProductName, productName);
        }
        if (typeId != null) {
            wrapper.eq(Product::getTypeId, typeId);
        }
        return productMapper.selectList(wrapper);
    }

    @Override
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("入库数量必须大于0");
        }
        product.setStock(product.getStock() + quantity);
        productMapper.updateById(product);
    }

    @Override
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("出库数量必须大于0");
        }
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足，当前库存: " + product.getStock());
        }
        product.setStock(product.getStock() - quantity);
        productMapper.updateById(product);
    }
}
