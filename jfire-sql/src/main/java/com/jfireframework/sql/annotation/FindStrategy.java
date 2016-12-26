package com.jfireframework.sql.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FindStrategy
{
    public String name();
    
    public String selectFields();
    
    public String whereFields();
}
