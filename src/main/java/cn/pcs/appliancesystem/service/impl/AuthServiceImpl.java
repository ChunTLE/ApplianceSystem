package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.LoginRequest;
import cn.pcs.appliancesystem.entity.LoginResponse;
import cn.pcs.appliancesystem.entity.RegisterRequest;
import cn.pcs.appliancesystem.entity.SysUser;
import cn.pcs.appliancesystem.entity.UserWithRole;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.mapper.SysUserMapper;
import cn.pcs.appliancesystem.service.AuthService;
import cn.pcs.appliancesystem.util.JwtUtil;
import cn.pcs.appliancesystem.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException("用户名和密码不能为空");
        }

        // 查询用户（包含角色信息）
        UserWithRole user = sysUserMapper.selectByUsernameWithRole(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        // 使用BCrypt验证密码
        String storedPassword = user.getPassword();
        
        // 检查密码格式
        if (storedPassword == null || storedPassword.trim().isEmpty()) {
            throw new BusinessException("用户密码未设置");
        }
        
        // 如果密码不是BCrypt格式，说明数据库中的密码格式不正确
        if (!passwordUtil.isEncoded(storedPassword)) {
            throw new BusinessException("密码格式错误，请联系管理员重置密码");
        }
        
        // 使用BCrypt验证密码
        if (!passwordUtil.matches(request.getPassword(), storedPassword)) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成Token
        String roleCode = user.getRoleCode() != null ? user.getRoleCode() : getRoleCodeByRoleId(user.getRoleId());
        String roleName = user.getRoleName() != null ? user.getRoleName() : getRoleNameByRoleId(user.getRoleId());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roleCode);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .roleId(user.getRoleId())
                .roleName(roleName)
                .roleCode(roleCode)
                .build();
    }

    private String getRoleCodeByRoleId(Long roleId) {
        if (roleId == 1L)
            return "ADMIN";
        if (roleId == 2L)
            return "STOCK";
        if (roleId == 3L)
            return "SALES";
        return "USER";
    }

    private String getRoleNameByRoleId(Long roleId) {
        if (roleId == 1L)
            return "管理员";
        if (roleId == 2L)
            return "库存人员";
        if (roleId == 3L)
            return "销售人员";
        return "用户";
    }

    @Override
    public void register(RegisterRequest request) {
        // 验证必填字段
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (request.getRoleId() == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 只能注册库存人员（roleId=2）或销售人员（roleId=3）
        if (request.getRoleId() != 2L && request.getRoleId() != 3L) {
            throw new BusinessException("只能注册库存人员或销售人员账号");
        }

        // 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername()));
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 验证密码长度（至少6位）
        if (request.getPassword().length() < 6) {
            throw new BusinessException("密码长度至少为6位");
        }

        // 创建新用户
        String encodedPassword = passwordUtil.encode(request.getPassword());
        SysUser newUser = SysUser.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .roleId(request.getRoleId())
                .status(1) // 默认启用
                .build();

        sysUserMapper.insert(newUser);
    }
}
