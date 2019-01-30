package org.cnbi.web.controller.MyLog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.cnbi.service.LogService;
import org.cnbi.utils.log.Log;
import org.cnbi.web.utils.ApplicationContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by FangQiang on 2019/1/29
 */
@Aspect
@Component
public class LogAspect {

    private final LogService<Log> logLogService;

    private final HttpServletRequest request;

    @Autowired
    public LogAspect(LogService<Log> logLogService, HttpServletRequest request) {
        this.logLogService = logLogService;
        this.request = request;
    }

    @Pointcut("execution(* org.cnbi.web.controller.riskController..*.*(..))")
    public void serviceAspect() {}

    @Before("serviceAspect()")
    public void doBefore(JoinPoint joinPoint) {
        doAfterThrowing(joinPoint, null);
    }

    @AfterThrowing(pointcut = "serviceAspect()", throwing = "throwable")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable throwable) {
        // String key = ApplicationContextUtil.getRequest().getHeader("token") + Thread.currentThread().getId();
        String key = ApplicationContextUtil.getRequest().getSession().getId() + Thread.currentThread().getId();
        AopLogUtils.saveLog(logLogService, key, joinPoint, throwable);
    }

    /**
     * 或者使用 {@link Around} 方法
     */
    @Around("serviceAspect()")
    public Object doAround(ProceedingJoinPoint point) {
        return AopLogUtils.saveLog(logLogService, point).getResult();
    }
}