package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存管理", description = "产品入库、出库操作接口")
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 产品入库
     */
    @Operation(
            summary = "产品入库",
            description = "执行产品入库操作，会增加产品库存并记录入库信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "入库成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "入库失败（产品不存在、参数错误等）",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    @PostMapping("/in")
    public Result<?> stockIn(
            @Parameter(description = "产品ID", required = true, example = "1")
            @RequestParam Long productId,
            @Parameter(description = "入库数量", required = true, example = "10")
            @RequestParam Integer quantity,
            @Parameter(description = "操作员ID", required = true, example = "1")
            @RequestParam Long operatorId) {
        stockService.stockIn(productId, quantity, operatorId);
        return Result.success();
    }

    /**
     * 产品出库
     */
    @Operation(
            summary = "产品出库",
            description = "执行产品出库操作，会减少产品库存并记录出库信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "出库成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "出库失败（产品不存在、库存不足、参数错误等）",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    @PostMapping("/out")
    public Result<?> stockOut(
            @Parameter(description = "产品ID", required = true, example = "1")
            @RequestParam Long productId,
            @Parameter(description = "出库数量", required = true, example = "5")
            @RequestParam Integer quantity,
            @Parameter(description = "操作员ID", required = true, example = "1")
            @RequestParam Long operatorId) {
        stockService.stockOut(productId, quantity, operatorId);
        return Result.success();
    }
}
