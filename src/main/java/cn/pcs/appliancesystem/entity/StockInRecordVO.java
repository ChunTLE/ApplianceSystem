package cn.pcs.appliancesystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 入库记录视图对象，用于显示入库记录信息
 */
@Schema(description = "入库记录信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockInRecordVO {

    @Schema(description = "入库记录ID", example = "1")
    private Long id;

    @Schema(description = "产品名称", example = "海尔双开门冰箱")
    private String productName;

    @Schema(description = "入库数量", example = "10")
    private Integer quantity;

    @Schema(description = "操作人", example = "张三")
    private String operator;

    @Schema(description = "入库时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inTime;
}