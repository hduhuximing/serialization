package com.jfireframework.swaggertool.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSchema
{
    public String name();
    
    public String type();
    
    public String format() default "";
    
    public String description() default "";
    
    public String title() default "";
    
    public ApiProperty[] properties() default {};
}
