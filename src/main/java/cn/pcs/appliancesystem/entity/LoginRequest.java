package cn.pcs.appliancesystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "登录请求")
@Data
public class LoginRequest {
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "密码", example = "123456")
    private String password;
}

