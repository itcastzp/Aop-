package org.cnbi.web.aop;

import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.cnbi.utils.pojo.JsonData;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 表单的Aop管理，防止重复提交
 */
@Aspect
@Component
@Log
public class FormAop {

    @Pointcut(value="@annotation(org.cnbi.web.aop.PreventDuplicateSubmissions)")
    public void point(){};

    @Around("point()")
    public Object validate(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = getRequest();
        String token = request.getParameter("token");
        if (StringUtils.isEmpty(token)) {
            log.info("没有携带Token");
            return JsonData.fail("没有携带Token");
        }

        if (TokenManagement.getToken(token) != null) {
            log.info("请勿重复提交");
            return JsonData.fail("请勿重复提交");
        }

        TokenManagement.invalidateToken(token);
        Object proceed = joinPoint.proceed();

        return proceed;
    }


    public HttpServletRequest getRequest() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;

    }


}
