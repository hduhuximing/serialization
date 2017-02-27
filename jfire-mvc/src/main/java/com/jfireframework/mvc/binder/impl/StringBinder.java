package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class StringBinder implements DataBinder
{
    
    private final String prefixName;
    
    public StringBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueNode, HttpServletResponse response)
    {
        ParamResolver node = treeValueNode.get(prefixName);
        if (node == null)
        {
            return null;
        }
        else
        {
            return ((StringValueResolver) node).getValue();
        }
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
