package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> listAll();

    Product getById(Long id);
    
    List<Product> search(String productName, Long typeId);
    
    /**
     * 获取所有产品（包括已下架的）
     * @return 所有产品列表
     */
    List<Product> getAllProducts();

    void increaseStock(Long productId, Integer quantity);

    void decreaseStock(Long productId, Integer quantity);

    boolean updateProduct(Product product);

    boolean deleteProduct(Long id);

    boolean createProduct(Product product);
}
