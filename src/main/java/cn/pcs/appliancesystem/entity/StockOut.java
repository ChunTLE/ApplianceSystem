package cn.pcs.appliancesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("stock_out")
public class StockOut {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Integer quantity;

    private Long operatorId;

    private LocalDateTime outTime;
}
