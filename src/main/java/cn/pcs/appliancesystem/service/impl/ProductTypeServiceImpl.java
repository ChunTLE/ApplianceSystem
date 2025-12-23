package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.ProductType;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.ProductTypeMapper;
import cn.pcs.appliancesystem.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {
    
    private final ProductTypeMapper productTypeMapper;
    
    @Override
    public List<ProductType> listAll() {
        return productTypeMapper.selectList(null);
    }
    
    @Override
    public ProductType getById(Long id) {
        ProductType type = productTypeMapper.selectById(id);
        if (type == null) {
            throw new BusinessException("产品类型不存在");
        }
        return type;
    }
    
    @Override
    public void save(ProductType productType) {
        if (productType.getTypeName() == null || productType.getTypeName().trim().isEmpty()) {
            throw new BusinessException("产品类型名称不能为空");
        }
        productTypeMapper.insert(productType);
    }
    
    @Override
    public void update(ProductType productType) {
        if (productType.getId() == null) {
            throw new BusinessException("产品类型ID不能为空");
        }
        ProductType exist = productTypeMapper.selectById(productType.getId());
        if (exist == null) {
            throw new BusinessException("产品类型不存在");
        }
        if (productType.getTypeName() == null || productType.getTypeName().trim().isEmpty()) {
            throw new BusinessException("产品类型名称不能为空");
        }
        productTypeMapper.updateById(productType);
    }
    
    @Override
    public void delete(Long id) {
        ProductType type = productTypeMapper.selectById(id);
        if (type == null) {
            throw new BusinessException("产品类型不存在");
        }
        productTypeMapper.deleteById(id);
    }
}

