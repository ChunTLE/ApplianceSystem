package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.StockInRecordVO;
import cn.pcs.appliancesystem.entity.StockOutRecordVO;

import java.util.List;

public interface StockService {

    void stockIn(Long productId, Integer quantity, Long operatorId);

    void stockOut(Long productId, Integer quantity, Long operatorId);
    
    /**
     * 获取所有入库记录
     * @return 入库记录列表
     */
    List<StockInRecordVO> getStockInRecords();
    
    /**
     * 获取所有出库记录
     * @return 出库记录列表
     */
    List<StockOutRecordVO> getStockOutRecords();
}