package cn.pcs.appliancesystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class Product {

    @TableId
    private Long id;

    private String productName;

    private Long typeId;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private LocalDateTime createTime;
}
