package cn.pcs.appliancesystem.config;

import cn.pcs.appliancesystem.exception.BusinessException;
import cn.pcs.appliancesystem.exception.TokenExpiredException;
import cn.pcs.appliancesystem.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * JWT拦截器
 * 用于验证Token和权限控制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    // 不需要认证的路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        
        // 排除不需要认证的路径
        if (EXCLUDE_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }
        
        // 获取Token
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        
        // 验证Token
        try {
            // 先检查Token是否过期
            if (jwtUtil.isTokenExpiredSafe(token)) {
                throw new TokenExpiredException("Token已过期，请重新登录");
            }
            
            // 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(401, "Token无效，请重新登录");
            }
            
            // 将用户信息存入request，供后续使用
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new TokenExpiredException("Token已过期，请重新登录");
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            throw new BusinessException(401, "Token验证失败，请重新登录");
        }
        
        return true;
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

