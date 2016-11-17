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
 * 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
 * 
 * @author linbin
 *
 */
public @interface Max
{
    public long value();
}
