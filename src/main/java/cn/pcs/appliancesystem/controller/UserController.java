package cn.pcs.appliancesystem.controller;

import cn.pcs.appliancesystem.entity.Result;
import cn.pcs.appliancesystem.entity.SysUser;
import cn.pcs.appliancesystem.mapper.SysUserMapper;
import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户管理接口（仅管理员）")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper sysUserMapper;
    private final PasswordUtil passwordUtil;

    @Operation(summary = "查询所有用户")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        List<SysUser> users = sysUserMapper.selectList(null);
        // 不返回密码字段，确保安全
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 不返回密码字段，确保安全
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<?> save(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        // 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, user.getUsername()));
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        String encodedPassword = passwordUtil.encode(user.getPassword());
        user.setPassword(encodedPassword);

        sysUserMapper.insert(user);
        return Result.success();
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<?> update(@RequestBody SysUser user) {
        if (user.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        SysUser exist = sysUserMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果提供了新密码，则加密
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            // 如果密码未加密，则进行加密
            if (!passwordUtil.isEncoded(user.getPassword())) {
                String encodedPassword = passwordUtil.encode(user.getPassword());
                user.setPassword(encodedPassword);
            }
        } else {
            // 如果没有提供密码，保持原密码不变
            user.setPassword(exist.getPassword());
        }

        sysUserMapper.updateById(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        sysUserMapper.deleteById(id);
        return Result.success();
    }
}
