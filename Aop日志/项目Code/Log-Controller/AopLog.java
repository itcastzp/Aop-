package org.cnbi.web.controller.Log;

import java.lang.annotation.*;

/**
 * Created by FangQiang on 2019/1/29
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AopLog {

    /**
     * 日志描述
     * @return
     */
    String value() default "";

    /**
     * 日志类型
     * @return
     */
    int type() default 1;

    /**
     * 日志级别
     * @return
     */
    int level() default 1;
}
