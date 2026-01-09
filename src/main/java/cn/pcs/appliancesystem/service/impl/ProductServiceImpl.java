package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.Product;
import cn.pcs.appliancesystem.entity.ProductType;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.ProductMapper;
import cn.pcs.appliancesystem.mapper.ProductTypeMapper;
import cn.pcs.appliancesystem.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductTypeMapper productTypeMapper;

    @Override
    public List<Product> listAll() {
        List<Product> products = productMapper.selectList(null);
        // 填充类型名称
        fillTypeName(products);
        return products;
    }

    /**
     * 填充产品类型名称
     */
    private void fillTypeName(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        // 获取所有类型ID（过滤掉null值）
        List<Long> typeIds = products.stream()
                .map(Product::getTypeId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (typeIds.isEmpty()) {
            log.warn("产品列表中没有有效的类型ID");
            return;
        }

        // 批量查询类型信息
        LambdaQueryWrapper<ProductType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.in(ProductType::getId, typeIds);
        List<ProductType> types = productTypeMapper.selectList(typeWrapper);
        log.debug("查询到的类型列表: {}", types);
        Map<Long, String> typeMap = types.stream()
                .collect(Collectors.toMap(ProductType::getId, ProductType::getTypeName));
        log.debug("类型映射表: {}", typeMap);

        // 填充类型名称
        products.forEach(product -> {
            String typeName = typeMap.get(product.getTypeId());
            log.debug("产品ID: {}, 类型ID: {}, 类型名称: {}", product.getId(), product.getTypeId(), typeName);
            product.setTypeName(typeName);
        });
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            fillTypeName(List.of(product));
        }
        return product;
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
        List<Product> products = productMapper.selectList(wrapper);
        // 填充类型名称
        fillTypeName(products);
        return products;
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

    @Override
    public boolean updateProduct(Product product) {
        // 检查产品是否存在
        Product existingProduct = productMapper.selectById(product.getId());
        if (existingProduct == null) {
            return false;
        }
        
        // 更新产品信息
        return productMapper.updateById(product) > 0;
    }

    @Override
    public boolean deleteProduct(Long id) {
        // 检查产品是否存在
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            return false;
        }
        
        // 删除产品
        return productMapper.deleteById(id) > 0;
    }

    @Override
    public boolean createProduct(Product product) {
        // 设置创建时间
        product.setCreateTime(java.time.LocalDateTime.now());
        
        // 新产品默认上架状态
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        
        // 如果库存未设置，默认为0
        if (product.getStock() == null) {
            product.setStock(0);
        }
        
        // 插入产品并返回是否成功
        return productMapper.insert(product) > 0;
    }
}
