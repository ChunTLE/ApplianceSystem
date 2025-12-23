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
@TableName("sale")
public class Sale {

    @TableId
    private Long id;

    private Long productId;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Long salesmanId;

    private LocalDateTime saleTime;
}
