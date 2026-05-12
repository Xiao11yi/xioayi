package com.olivia.xioayi.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /** 模块标题 */
    String title() default "";
    
    /** 业务类型（如：新增、修改、删除等） */
    BusinessType businessType() default BusinessType.OTHER;
}