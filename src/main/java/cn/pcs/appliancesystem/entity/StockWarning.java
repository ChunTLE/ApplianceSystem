package cn.pcs.appliancesystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "库存预警信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockWarning {
    @Schema(description = "产品ID")
    private Long productId;
    
    @Schema(description = "产品名称")
    private String productName;
    
    @Schema(description = "当前库存")
    private Integer stock;
    
    @Schema(description = "预警阈值")
    private Integer threshold;
    
    @Schema(description = "预警级别（1-低库存，2-缺货）")
    private Integer level;
}

