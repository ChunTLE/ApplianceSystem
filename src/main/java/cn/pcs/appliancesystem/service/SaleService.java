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
}