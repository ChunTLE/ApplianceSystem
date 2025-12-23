package cn.pcs.appliancesystem.exception;

import lombok.Getter;

/**
 * Token过期异常
 * 用于明确标识Token过期的情况
 */
@Getter
public class TokenExpiredException extends BusinessException {
    
    public TokenExpiredException(String message) {
        super(401, message);
    }
}

