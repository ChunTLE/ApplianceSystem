package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.ProductType;
import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.service.ProductTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "产品类型管理", description = "产品类型管理接口（仅管理员）")
@RestController
@RequestMapping("/api/product-type")
@RequiredArgsConstructor
public class ProductTypeController {
    
    private final ProductTypeService productTypeService;
    
    @Operation(summary = "查询所有产品类型")
    @GetMapping("/list")
    public Result<List<ProductType>> list() {
        return Result.success(productTypeService.listAll());
    }
    
    @Operation(summary = "根据ID查询产品类型")
    @GetMapping("/{id}")
    public Result<ProductType> getById(@PathVariable Long id) {
        return Result.success(productTypeService.getById(id));
    }
    
    @Operation(summary = "新增产品类型")
    @PostMapping
    public Result<?> save(@RequestBody ProductType productType) {
        productTypeService.save(productType);
        return Result.success();
    }
    
    @Operation(summary = "更新产品类型")
    @PutMapping
    public Result<?> update(@RequestBody ProductType productType) {
        productTypeService.update(productType);
        return Result.success();
    }
    
    @Operation(summary = "删除产品类型")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        productTypeService.delete(id);
        return Result.success();
    }
}

