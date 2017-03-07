package com.jfireframework.swaggertool.anno;

public @interface ApiProperty
{
    public String name() default "";
    
    public String type();
    
    public String description();
}
