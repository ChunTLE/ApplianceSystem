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
    
    @Operation(summary = "搜索产品", description = "根据产品名称和类型ID搜索产品")
    @GetMapping("/search")
    public Result<List<Product>> search(
            @Parameter(description = "产品名称（模糊查询）", example = "冰箱")
            @RequestParam(required = false) String productName,
            @Parameter(description = "产品类型ID", example = "1")
            @RequestParam(required = false) Long typeId) {
        return Result.success(productService.search(productName, typeId));
    }

    @Operation(summary = "更新产品", description = "根据产品ID更新产品信息")
    @PutMapping("/{id}")
    public Result<String> update(
            @Parameter(description = "产品ID", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Product product) {
        // 设置产品ID，防止路径参数与请求体中的ID不一致
        product.setId(id);
        boolean success = productService.updateProduct(product);
        if (success) {
            return Result.success("产品更新成功");
        } else {
            return Result.error("产品不存在或更新失败");
        }
    }

    @Operation(summary = "删除产品", description = "根据产品ID删除产品")
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @Parameter(description = "产品ID", required = true, example = "1")
            @PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        if (success) {
            return Result.success("产品删除成功");
        } else {
            return Result.error("产品不存在或删除失败");
        }
    }
}
