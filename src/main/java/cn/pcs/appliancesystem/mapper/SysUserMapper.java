package cn.pcs.appliancesystem.mapper;

import cn.pcs.appliancesystem.entity.SysUser;
import cn.pcs.appliancesystem.entity.UserWithRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT u.*, r.role_code, r.role_name FROM sys_user u " +
            "LEFT JOIN sys_role r ON u.role_id = r.id " +
            "WHERE u.username = #{username} AND u.status = 1")
    UserWithRole selectByUsernameWithRole(String username);
}
