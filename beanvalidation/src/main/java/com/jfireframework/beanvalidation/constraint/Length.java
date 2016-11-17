package com.jfireframework.beanvalidation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
/**
 * 注解的字符串长度不能超过value
 * 
 * @author 林斌
 *
 */
public @interface Length
{
    public int value();
}
