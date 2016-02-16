package com.jhhc.baseframework.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试条件不能用于session的保持，用这个来加入session，测试时用
 *
 * @author yecq
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MakeLogged {

    // 内容为各个用户的用户名
    public String[] value() default "yecq";
}
