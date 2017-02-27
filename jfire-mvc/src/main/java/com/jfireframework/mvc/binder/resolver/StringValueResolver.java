package com.jfireframework.mvc.binder.resolver;

public class StringValueResolver implements ParamResolver
{
    private final String value;
    
    public StringValueResolver(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}
