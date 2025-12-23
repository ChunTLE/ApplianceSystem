package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.Product;
import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.service.ProductService;
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

@Tag(name = "产品管理", description = "产品信息查询和管理接口")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 查询所有产品
     */
    @Operation(
            summary = "查询所有产品",
            description = "获取系统中所有产品的列表信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.success(productService.listAll());
    }

    /**
     * 根据ID查询产品
     */
    @Operation(
            summary = "根据ID查询产品",
            description = "根据产品ID获取单个产品的详细信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "产品不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    @GetMapping("/{id}")
    public Result<Product> getById(
            @Parameter(description = "产品ID", required = true, example = "1")
            @PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("产品不存在");
        }
        return Result.success(product);
    }
}
