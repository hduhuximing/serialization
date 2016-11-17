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
 * 被注释的元素的大小必须在指定的范围内(闭区间)
 * 
 * @author linbin
 *
 */
public @interface Size
{
    public long max();
    
    public long min();
}
