package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.StockWarning;

import java.util.List;

public interface StockWarningService {
    /**
     * 获取库存预警列表
     * @param threshold 预警阈值，默认10
     */
    List<StockWarning> getWarningList(Integer threshold);
}

