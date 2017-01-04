package com.jfireframework.sql.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface SqlStrategy
{
    public String valueFields();
    
    public String whereFields();
}
