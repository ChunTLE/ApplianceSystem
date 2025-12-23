package cn.pcs.appliancesystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 */
@Component
public class JwtUtil {

    // JWT密钥（实际项目中应该从配置文件读取）
    // 注意：密钥长度必须至少32字节（256位）用于HS256算法
    private static final String SECRET_KEY = "AppliancesSystemSecretKey2025ForJWTTokenGeneration";

    // 令牌过期时间（24小时）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    // 密钥对象（延迟初始化）
    private SecretKey key;

    /**
     * 初始化密钥（在Bean创建后执行）
     */
    @PostConstruct
    public void init() {
        // 确保密钥长度至少32字节
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // 如果密钥长度不足，进行填充
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            this.key = Keys.hmacShaKeyFor(paddedKey);
        } else {
            // 如果密钥长度足够，直接使用前32字节
            byte[] truncatedKey = new byte[32];
            System.arraycopy(keyBytes, 0, truncatedKey, 0, 32);
            this.key = Keys.hmacShaKeyFor(truncatedKey);
        }
    }

    /**
     * 生成JWT令牌
     * 
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色
     * @return JWT令牌
     */
    public String generateToken(Long userId, String username, String role) {
        if (key == null) {
            throw new IllegalStateException("JWT密钥未初始化");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取用户角色
     * 
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 获取令牌过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取指定声明
     * 
     * @param token          JWT令牌
     * @param claimsResolver 声明解析器
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     * 
     * @param token JWT令牌
     * @return 所有声明
     */
    public Claims getAllClaimsFromToken(String token) {
        if (key == null) {
            throw new IllegalStateException("JWT密钥未初始化");
        }
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查令牌是否过期
     * 
     * @param token JWT令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 验证令牌
     * 
     * @param token    JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    /**
     * 验证令牌（不检查用户名）
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        if (key == null) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从Authorization头中提取令牌
     * 
     * @param authHeader Authorization头
     * @return JWT令牌
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}