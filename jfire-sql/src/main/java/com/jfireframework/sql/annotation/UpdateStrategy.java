package com.jfireframework.sql.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface UpdateStrategy
{
    public String name();
    
    public String setFields();
    
    public String whereFields();
}
