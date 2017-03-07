package com.jfireframework.swaggertool.anno;

public @interface ApiParameter
{
    public static enum in
    {
        path, query, header, body, formData
    }
    
    public String name();
    
    public in in();
    
    public String description() default "";
    
    public boolean required();
    
    public String type();
    
    public String format() default "";
    
}
