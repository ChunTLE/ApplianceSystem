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
}

