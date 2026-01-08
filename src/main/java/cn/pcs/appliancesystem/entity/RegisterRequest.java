package cn.pcs.appliancesystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "注册请求")
@Data
public class RegisterRequest {
    @Schema(description = "用户名", example = "stock01")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "角色ID（2-库存人员，3-销售人员）", example = "2")
    private Long roleId;
}
