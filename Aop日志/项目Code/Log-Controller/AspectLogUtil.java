package org.cnbi.web.controller.Log;


import com.alibaba.fastjson.JSONArray;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.cnbi.entity.User;
import org.cnbi.service.LogService;
import org.cnbi.utils.log.LogBean;
import org.cnbi.web.utils.ApplicationContextUtil;
import org.cnbi.web.utils.BrowserType;
import org.cnbi.web.utils.BrowserUtils;
import org.cnbi.web.utils.IpUtil;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

public class AspectLogUtil {

    /**
     * 保存日志 用于前置通知和异常通知公用的
     *
     * @param logService            服务类
     * @param joinPoint             切点类
     * @param throwable             异常
     * @param <T>                   日志类
     */
    public static<T> void saveLog(LogService<T> logService, JoinPoint joinPoint, Throwable throwable) {

        LogBean logBean = setLogBean(joinPoint);
        if (logBean == null) return;

        LinkedHashMap<String, String> map = setLogOtherInfo();

        // 保存一下
        if(throwable == null) {
            T log = logService.getLog(logBean, map, null);
            logService.save(log);
        } else {
            T log = logService.getLog(logBean, map, throwable);
            logService.saveException(log, throwable);
        }

    }

    /**
     * 设置其他信息，因为Service层没有直接获取到request的方法，所以在这里写了
     *
     * @return
     */
    private static LinkedHashMap<String, String> setLogOtherInfo() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        User user = ApplicationContextUtil.getCurrentUser();
        String ip = IpUtil.getIpAddr(ApplicationContextUtil.getRequest());
        BrowserType browser = BrowserUtils.getBrowserType(ApplicationContextUtil.getRequest());
        map.put("username", user.getUsername());
        map.put("ip", ip);
        map.put("browser", browser.name());
        return map;
    }

    /**
     * 利用反射获取信息，设置日志Bean
     *
     * @param joinPoint     切点
     *
     * @return
     */
    private static LogBean setLogBean(JoinPoint joinPoint) {
        LogBean logBean = new LogBean();
        String args = JSONArray.toJSONString(joinPoint.getArgs());
        logBean.setArgs(args);
        Class<?> clazz = joinPoint.getTarget().getClass();
        logBean.setClassName(clazz.getName()).setMethodName(joinPoint.getSignature().getName());

        // 获取注解信息，设置到LogBean
        for (Method method : clazz.getDeclaredMethods()) {
            if(method.getName().equals(logBean.getMethodName())) {
                AopLog annotation = method.getAnnotation(AopLog.class);
                if(annotation != null && !"".equals(annotation.value())) {
                    logBean.setDescription(annotation.value());
                    logBean.setLevel(annotation.level());
                    logBean.setType(annotation.type());
                } else {
                    // 注解没有信息不需要保存日志
                    return null;
                }
            }
        }
        logBean.setExecutedTime(System.currentTimeMillis());
        return logBean;
    }

    /**
     * 保存日志，不抛出异常   用于环绕通知
     *
     * @param logService    日志服务
     * @param point         切点
     * @param <K>
     *
     * @return
     */
    public static <K> LogExBean<K> saveLog(LogService<K> logService, ProceedingJoinPoint point) {
        LogExBean<K> logExBean = proceedAround(logService, point, true);
        //if (logExBean.getThrowable() == null) {
            return logExBean;
        //}
        //throw logExBean.getThrowable();
    }

    /**
     * 执行方法         用于环绕通知
     *
     * @param service 日志服务 {@link LogService}
     * @param point 切点  {@link ProceedingJoinPoint}
     * @param saveLog 是否保存日志
     * @param <T> 日志表
     *
     * @return {@link LogExBean}
     *
     * @since 1.0.4
     */
    private static <T> LogExBean<T> proceedAround(LogService<T> service, ProceedingJoinPoint point, boolean saveLog) {
        // 获取日志信息
        LogBean logBean = setLogBean(point);
        // 没有注解不保存日志
        LinkedHashMap<String, String> map = setLogOtherInfo();
        Throwable t = null;
        long beginTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行方法
            result = point.proceed();
        } catch (Throwable e) {
            t = e;
        }

        T log = null;
        if(logBean != null) {
            logBean.setExecutedTime(System.currentTimeMillis() - beginTime);
            log = service.getLog(logBean, map, t);
            // 保存日志
            if (saveLog) {
                if (t == null) {
                    service.save(log);
                } else {
                    service.saveException(log, t);
                }
            }
        }
        return new LogExBean<>(log, t, result);
    }
}
