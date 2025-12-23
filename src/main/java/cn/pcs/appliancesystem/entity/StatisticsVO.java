package cn.pcs.appliancesystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "统计信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsVO {
    @Schema(description = "日期/产品名称等")
    private String label;
    
    @Schema(description = "数量")
    private Integer count;
    
    @Schema(description = "总金额")
    private BigDecimal totalAmount;
}

