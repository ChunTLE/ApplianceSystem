package cn.pcs.appliancesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "产品信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class Product {

    @Schema(description = "产品ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品名称", example = "海尔冰箱")
    private String productName;

    @Schema(description = "产品类型ID", example = "1")
    private Long typeId;

    @Schema(description = "产品类型名称", example = "冰箱")
    @TableField(exist = false)  // 表示该字段不是数据库字段
    private String typeName;

    @Schema(description = "产品价格", example = "2999.00")
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "产品状态（0-下架，1-上架）", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2025-01-01T10:00:00")
    private LocalDateTime createTime;
}
