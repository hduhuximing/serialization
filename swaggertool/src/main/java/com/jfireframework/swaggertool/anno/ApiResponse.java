package com.jfireframework.swaggertool.anno;

public @interface ApiResponse
{
    public String code();
    
    public String description() default "";
    
    public String schema();
}
