package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class HttpServletResponseBinder implements DataBinder
{
    private final String prefixName;
    
    public HttpServletResponseBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueNode, HttpServletResponse response)
    {
        return response;
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
