package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.StatisticsVO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    /**
     * 入库统计（按日期）
     */
    List<StatisticsVO> getStockInStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * 出库统计（按日期）
     */
    List<StatisticsVO> getStockOutStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * 销售统计（按日期）
     */
    List<StatisticsVO> getSaleStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * 销售统计（按产品）
     */
    List<StatisticsVO> getSaleStatisticsByProduct(LocalDate startDate, LocalDate endDate);
}

