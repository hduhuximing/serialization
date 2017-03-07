package com.jfireframework.swaggertool.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSchemas
{
    public ApiSchema[] value();
}
