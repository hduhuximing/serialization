package com.jfireframework.boot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
public @interface AppInfo
{
    public String appName();
    
    public String prefix() default "web";
    
    public int port() default 80;
    
    public boolean hotdev() default false;
    
    public String monitorPath() default "";
    
    public String reloadPath() default "";
    
    public String reploadPackages() default "";
    
    public String excludePackages() default "";
}
