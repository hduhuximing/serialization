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
 * 被注释的元素必须符合指定的正则表达式
 * 
 * @author linbin
 *
 */
public @interface Pattern
{
    public String value();
}
