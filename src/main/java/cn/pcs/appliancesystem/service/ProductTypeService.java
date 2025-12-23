package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.ProductType;

import java.util.List;

public interface ProductTypeService {
    List<ProductType> listAll();
    
    ProductType getById(Long id);
    
    void save(ProductType productType);
    
    void update(ProductType productType);
    
    void delete(Long id);
}

