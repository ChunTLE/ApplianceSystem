package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.entity.StockWarning;
import cn.pcs.appliancesystem.service.StockWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库存预警", description = "库存预警查询接口")
@RestController
@RequestMapping("/api/stock-warning")
@RequiredArgsConstructor
public class StockWarningController {
    
    private final StockWarningService stockWarningService;
    
    @Operation(summary = "获取库存预警列表")
    @GetMapping("/list")
    public Result<List<StockWarning>> getWarningList(
            @Parameter(description = "预警阈值，默认10", example = "10")
            @RequestParam(required = false) Integer threshold) {
        return Result.success(stockWarningService.getWarningList(threshold));
    }
}

