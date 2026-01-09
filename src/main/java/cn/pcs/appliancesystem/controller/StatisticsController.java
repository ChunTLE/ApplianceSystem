package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.entity.StatisticsVO;
import cn.pcs.appliancesystem.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "统计管理", description = "统计信息查询接口（仅管理员）")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "入库统计（按日期）")
    @GetMapping("/stock-in")
    public Result<List<StatisticsVO>> getStockInStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getStockInStatistics(startDate, endDate));
    }
    
    @Operation(summary = "出库统计（按日期）")
    @GetMapping("/stock-out")
    public Result<List<StatisticsVO>> getStockOutStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getStockOutStatistics(startDate, endDate));
    }
    
    @Operation(summary = "销售统计（按日期）")
    @GetMapping("/sale")
    public Result<List<StatisticsVO>> getSaleStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getSaleStatistics(startDate, endDate));
    }
    
    @Operation(summary = "销售统计（按产品）")
    @GetMapping("/sale-by-product")
    public Result<List<StatisticsVO>> getSaleStatisticsByProduct(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getSaleStatisticsByProduct(startDate, endDate));
    }
    
    @Operation(summary = "入库统计图表数据（按日期）")
    @GetMapping("/stock-in-chart")
    public Result<Map<String, Object>> getStockInChartStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<StatisticsVO> statistics = statisticsService.getStockInStatistics(startDate, endDate);
        
        // 准备图表数据
        List<String> dates = new java.util.ArrayList<>();
        List<Integer> counts = new java.util.ArrayList<>();
        List<String> productNames = new java.util.ArrayList<>();
        
        for (StatisticsVO stat : statistics) {
            dates.add(stat.getLabel());
            counts.add(stat.getCount());
            productNames.add(stat.getProductName() != null ? stat.getProductName() : "");
        }
        
        Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("dates", dates);
        chartData.put("counts", counts);
        chartData.put("productNames", productNames);
        
        return Result.success(chartData);
    }
    
    @Operation(summary = "出库统计图表数据（按日期）")
    @GetMapping("/stock-out-chart")
    public Result<Map<String, Object>> getStockOutChartStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<StatisticsVO> statistics = statisticsService.getStockOutStatistics(startDate, endDate);
        
        // 准备图表数据
        List<String> dates = new java.util.ArrayList<>();
        List<Integer> counts = new java.util.ArrayList<>();
        List<String> productNames = new java.util.ArrayList<>();
        
        for (StatisticsVO stat : statistics) {
            dates.add(stat.getLabel());
            counts.add(stat.getCount());
            productNames.add(stat.getProductName() != null ? stat.getProductName() : "");
        }
        
        Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("dates", dates);
        chartData.put("counts", counts);
        chartData.put("productNames", productNames);
        
        return Result.success(chartData);
    }
    
    @Operation(summary = "销售统计图表数据（按日期）")
    @GetMapping("/sale-chart")
    public Result<Map<String, Object>> getSaleChartStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<StatisticsVO> statistics = statisticsService.getSaleStatistics(startDate, endDate);
        
        // 准备图表数据
        List<String> labels = new java.util.ArrayList<>();
        List<Integer> quantities = new java.util.ArrayList<>();
        List<String> productNames = new java.util.ArrayList<>();
        List<java.math.BigDecimal> amounts = new java.util.ArrayList<>();
        
        for (StatisticsVO stat : statistics) {
            labels.add(stat.getLabel());
            quantities.add(stat.getCount());
            productNames.add(stat.getProductName() != null ? stat.getProductName() : "");
            amounts.add(stat.getTotalAmount());
        }
        
        Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("dates", labels);
        chartData.put("quantities", quantities);
        chartData.put("productNames", productNames);
        chartData.put("amounts", amounts);
        
        return Result.success(chartData);
    }
    
    @Operation(summary = "销售统计详细数据", description = "获取每个产品名称、时间、销售数量、金额的详细信息")
    @GetMapping("/sale-detail")
    public Result<List<Map<String, Object>>> getSaleDetailStatistics(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<StatisticsVO> statistics = statisticsService.getSaleStatistics(startDate, endDate);
        
        // 准备详细数据列表
        List<Map<String, Object>> detailList = new java.util.ArrayList<>();
        for (StatisticsVO stat : statistics) {
            Map<String, Object> detail = new java.util.HashMap<>();
            detail.put("productName", stat.getProductName());
            detail.put("date", stat.getLabel());
            detail.put("quantity", stat.getCount());
            detail.put("amount", stat.getTotalAmount());
            
            detailList.add(detail);
        }
        
        return Result.success(detailList);
    }
}

