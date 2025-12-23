package cn.pcs.appliancesystem.service;

public interface SaleService {

    void sell(Long productId, Integer quantity, Long salesmanId);
}
