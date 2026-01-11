package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.entity.SaleRecordVO;
import cn.pcs.appliancesystem.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "销售管理", description = "产品销售记录和管理接口")
@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    /**
     * 销售产品
     */
    @Operation(
            summary = "销售产品",
            description = "执行产品销售操作，会自动减少产品库存并记录销售信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "销售成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "销售失败（产品不存在、库存不足、参数错误等）",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    @PostMapping("/sell")
    public Result<?> sell(
            @Parameter(description = "产品ID", required = true, example = "1")
            @RequestParam Long productId,
            @Parameter(description = "销售数量", required = true, example = "5")
            @RequestParam Integer quantity,
            @Parameter(description = "销售员ID", required = true, example = "1")
            @RequestParam Long salesmanId) {
        saleService.sell(productId, quantity, salesmanId);
        return Result.success();
    }

    /**
     * 获取销售记录列表
     */
    @Operation(
            summary = "获取销售记录列表",
            description = "获取所有销售记录，包括产品名称、数量、总价格、销售员等信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    @GetMapping("/records")
    public Result<List<SaleRecordVO>> getSaleRecords() {
        List<SaleRecordVO> records = saleService.getSaleRecords();
        return Result.success(records);
    }
}