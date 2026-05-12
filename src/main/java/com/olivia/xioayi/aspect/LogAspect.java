package com.olivia.xioayi.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.dao.SysOperLog;
import com.olivia.xioayi.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperLogService logService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 切点：拦截所有标注了 @Log 注解的方法
     */
    @Pointcut("@annotation(com.olivia.xioayi.annotation.Log)")
    public void logPointCut() {}

    /**
     * 方法正常执行后记录日志
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint, null, result);
    }

    /**
     * 方法抛出异常时记录日志
     */
    @AfterThrowing(pointcut = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    /**
     * 组装日志信息并保存
     */
    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object result) {
        try {
            // 获取 @Log 注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log log = method.getAnnotation(Log.class);
            if (log == null) return;

            SysOperLog operLog = new SysOperLog();
            operLog.setTitle(log.title());
            operLog.setBusinessType(log.businessType().ordinal());
            operLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());

            // 获取请求中的用户信息【47†L32-L33】
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                operLog.setOperName(authentication.getName());
            }

            // 获取 Request 信息【47†L6-L7】
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.setOperUrl(request.getRequestURI());
                operLog.setOperIp(request.getRemoteAddr());
                operLog.setRequestMethod(request.getMethod());
            }

            // 保存请求参数（JSON 格式）
            operLog.setOperParam(objectMapper.writeValueAsString(joinPoint.getArgs()));
            // 保存返回结果
            operLog.setJsonResult(objectMapper.writeValueAsString(result));
            // 保存操作时间
            operLog.setOperTime(new Date());

            if (e != null) {
                operLog.setStatus(1); // 失败
                operLog.setErrorMsg(e.getMessage());
            } else {
                operLog.setStatus(0); // 成功
            }

            // 异步保存日志，避免阻塞主线程
            logService.saveLog(operLog);
        } catch (Exception ex) {
            // 日志记录过程本身出错，不影响业务
            ex.printStackTrace();
        }
    }
}