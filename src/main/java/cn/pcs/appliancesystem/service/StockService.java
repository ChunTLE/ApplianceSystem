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
    
    /**
     * 根据ID更新入库记录数量
     * @param id 入库记录ID
     * @param quantity 新的数量
     */
    void updateStockIn(Long id, Integer quantity);
    
    /**
     * 根据ID删除入库记录
     * @param id 入库记录ID
     */
    void deleteStockIn(Long id);
    
    /**
     * 根据ID更新出库记录数量
     * @param id 出库记录ID
     * @param quantity 新的数量
     */
    void updateStockOut(Long id, Integer quantity);
    
    /**
     * 根据ID删除出库记录
     * @param id 出库记录ID
     */
    void deleteStockOut(Long id);
}