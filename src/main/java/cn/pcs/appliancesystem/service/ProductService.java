package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> listAll();

    Product getById(Long id);

    void increaseStock(Long productId, Integer quantity);

    void decreaseStock(Long productId, Integer quantity);
}
