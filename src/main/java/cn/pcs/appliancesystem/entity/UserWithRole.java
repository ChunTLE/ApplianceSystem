package cn.pcs.appliancesystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithRole extends SysUser {
    private String roleCode;
    private String roleName;
}

