package cn.pcs.appliancesystem.service;

public interface StockService {

    void stockIn(Long productId, Integer quantity, Long operatorId);

    void stockOut(Long productId, Integer quantity, Long operatorId);
}
