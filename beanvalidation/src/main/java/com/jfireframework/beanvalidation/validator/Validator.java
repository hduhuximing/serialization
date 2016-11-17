package com.jfireframework.beanvalidation.validator;

public interface Validator
{
    /**
     * 该验证器支持的
     * 
     * @return
     */
    public Class<?> support();
}
