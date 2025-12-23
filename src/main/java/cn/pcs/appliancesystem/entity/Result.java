package cn.pcs.appliancesystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "统一响应结果")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    @Schema(description = "响应码（0-成功，非0-失败）", example = "0")
    private Integer code;
    
    @Schema(description = "响应消息", example = "操作成功")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;

    public static <E> Result<E> success(E data) {
        return new Result<>(0, "操作成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "操作成功", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(1, message, null);
    }
}
