package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.SaleRecordVO;

import java.util.List;

public interface SaleService {

    void sell(Long productId, Integer quantity, Long salesmanId);
    
    /**
     * 获取所有销售记录
     * @return 销售记录列表
     */
    List<SaleRecordVO> getSaleRecords();
    
    /**
     * 根据ID更新销售记录数量
     * @param id 销售记录ID
     * @param quantity 新的数量
     */
    void updateSale(Long id, Integer quantity);
    
    /**
     * 根据ID删除销售记录
     * @param id 销售记录ID
     */
    void deleteSale(Long id);
}