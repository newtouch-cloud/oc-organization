package com.geeke.ten.annotation;

import java.lang.annotation.*;

/**
 * @author TBH
 * 创建数据库
 */

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateAppDatabase {
    boolean database() default true;  //默认是创建数据库
}
