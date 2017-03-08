package com.jfireframework.swaggertool.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiOperation
{
    public String path() default "";
    
    public String method() default "";
    
    public String[] tags();
    
    /**
     * 简要介绍
     * 
     * @return
     */
    public String summary();
    
    /**
     * 详细介绍
     * 
     * @return
     */
    public String description() default "";
    
    public MIME[] consumes() default { MIME.formUrlencoded };
    
    public MIME[] produces() default { MIME.json };
    
    public String[] schemes() default { "http" };
    
    public boolean deprecated() default false;
    
}
