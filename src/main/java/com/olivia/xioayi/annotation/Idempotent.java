package com.olivia.xioayi.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /** Redis key 前缀 */
    String prefix() default "idempotent";

    /** 防抖时间窗口（秒），默认 2 秒内重复请求被拦截 */
    int ttl() default 2;
}
