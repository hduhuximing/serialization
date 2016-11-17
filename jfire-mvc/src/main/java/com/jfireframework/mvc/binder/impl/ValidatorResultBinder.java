package com.jfireframework.mvc.binder.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.beanvalidation.ValidResult;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public class ValidatorResultBinder implements DataBinder
{
    private final String paramName;
    
    public ValidatorResultBinder(String paramName)
    {
        this.paramName = paramName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        return new ValidResult();
    }
    
    @Override
    public String getParamName()
    {
        return paramName;
    }
    
}
