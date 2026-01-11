package cn.pcs.appliancesystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售记录视图对象，用于显示销售记录信息
 */
@Schema(description = "销售记录信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRecordVO {

    @Schema(description = "销售记录ID", example = "1")
    private Long id;

    @Schema(description = "产品名称", example = "海尔双开门冰箱")
    private String productName;

    @Schema(description = "销售数量", example = "2")
    private Integer quantity;

    @Schema(description = "总价格", example = "9998.00")
    private BigDecimal totalPrice;

    @Schema(description = "销售员", example = "张三")
    private String salesman;

    @Schema(description = "销售时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleTime;
}