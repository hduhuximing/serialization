package com.jfireframework.beanvalidation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
/**
 * 被注释的字符串的大小必须在指定的范围内
 * 
 * @author linbin
 *
 */
public @interface Length
{
    public int value();
}
