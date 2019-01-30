package org.cnbi.web.controller.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.cnbi.service.LogService;
import org.cnbi.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;

@Component
@Aspect
//@Order(Ordered.HIGHEST_PRECEDENCE)  // 最先执行
public class LogAspect {

    private final LogService<Log> logService;

    @Autowired
    public LogAspect(LogService<Log> logLogService) {
        this.logService = logLogService;
    }

    /**
     * 所有有RequestMapping注解的类当做切入类
     */
    //@Pointcut("@target(org.springframework.web.bind.annotation.RequestMapping)")
    @Pointcut("execution(* org.cnbi.web.controller.riskController..*.*(..))")
    public void controllerAspect() {}

    /*@Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        // 记录日志
        AfterThrowing(joinPoint, null);
    }

    @AfterThrowing(pointcut = "controllerAspect()", throwing = "throwable")
    public void AfterThrowing(JoinPoint joinPoint, Throwable throwable) {
        AspectLogUtil.saveLog(logService, joinPoint, throwable);
    }*/

    /**
     * 或者使用 {@link Around} 方法
     */
    @Around("controllerAspect()")
    public Object doAround(ProceedingJoinPoint point) throws JAXBException {
        return AspectLogUtil.saveLog(logService, point).getResult();
    }

}
