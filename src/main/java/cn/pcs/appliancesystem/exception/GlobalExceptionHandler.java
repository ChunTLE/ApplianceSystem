package cn.pcs.appliancesystem.exception;

import cn.pcs.appliancesystem.entity.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中所有异常，返回统一的响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletResponse response) {
        log.warn("业务异常: {}", e.getMessage());
        // 如果是401错误，返回HTTP 401状态码
        if (e.getCode() != null && e.getCode() == 401) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return new Result<>(401, e.getMessage(), null);
        }
        // 其他业务异常返回400
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new Result<>(e.getCode(), e.getMessage(), null);
    }

    /**
     * 处理Token过期异常
     */
    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleTokenExpiredException(TokenExpiredException e) {
        log.warn("Token过期: {}", e.getMessage());
        return new Result<>(401, e.getMessage(), null);
    }

    /**
     * 处理参数校验异常（@Valid 注解校验失败）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", errorMessage);
        return Result.error("参数校验失败: " + errorMessage);
    }

    /**
     * 处理绑定异常（表单数据绑定失败）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("数据绑定失败: {}", errorMessage);
        return Result.error("数据绑定失败: " + errorMessage);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配: {}", errorMessage);
        return Result.error(errorMessage);
    }

    /**
     * 处理数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常", e);
        
        // 检查是否是外键约束异常
        Throwable cause = e.getCause();
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            String message = cause.getMessage();
            if (message != null && message.contains("foreign key constraint")) {
                if (message.contains("fk_sale_user") || message.contains("salesman_id")) {
                    return Result.error("无法删除该用户，该用户存在销售记录，请先删除相关销售记录");
                } else if (message.contains("fk_stockin_user") || message.contains("stock_in")) {
                    return Result.error("无法删除该用户，该用户存在入库记录，请先删除相关入库记录");
                } else if (message.contains("fk_stockout_user") || message.contains("stock_out")) {
                    return Result.error("无法删除该用户，该用户存在出库记录，请先删除相关出库记录");
                } else {
                    return Result.error("无法删除该数据，存在关联数据，请先删除相关记录");
                }
            }
        }
        
        return Result.error("数据库操作失败，请稍后重试");
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleSQLException(SQLException e) {
        log.error("SQL异常", e);
        // 检查是否是外键约束异常
        if (e instanceof SQLIntegrityConstraintViolationException) {
            String message = e.getMessage();
            if (message != null && message.contains("foreign key constraint")) {
                if (message.contains("fk_sale_user") || message.contains("salesman_id")) {
                    return Result.error("无法删除该用户，该用户存在销售记录，请先删除相关销售记录");
                } else if (message.contains("fk_stockin_user") || message.contains("stock_in")) {
                    return Result.error("无法删除该用户，该用户存在入库记录，请先删除相关入库记录");
                } else if (message.contains("fk_stockout_user") || message.contains("stock_out")) {
                    return Result.error("无法删除该用户，该用户存在出库记录，请先删除相关出库记录");
                } else {
                    return Result.error("无法删除该数据，存在关联数据，请先删除相关记录");
                }
            }
        }
        return Result.error("数据库操作失败，请稍后重试");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error("系统内部错误，请联系管理员");
    }

    /**
     * 处理运行时异常（兜底处理）
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error("系统异常: " + e.getMessage());
    }

    /**
     * 处理所有其他异常（最终兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统内部错误，请联系管理员");
    }
}
