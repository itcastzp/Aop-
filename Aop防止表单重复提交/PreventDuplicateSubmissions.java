package org.cnbi.web.aop;

import java.lang.annotation.*;

/**
 * 防止表单重复提交注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Documented
public @interface PreventDuplicateSubmissions {

}
