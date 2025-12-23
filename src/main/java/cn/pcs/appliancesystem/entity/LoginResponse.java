package cn.pcs.appliancesystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "登录响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "Token")
    private String token;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "角色ID")
    private Long roleId;
    
    @Schema(description = "角色名称")
    private String roleName;
    
    @Schema(description = "角色代码")
    private String roleCode;
}

