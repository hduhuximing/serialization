package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;
import com.jfireframework.mvc.binder.transfer.Transfer;
import com.jfireframework.mvc.binder.transfer.TransferFactory;

public class WrapperBinder implements DataBinder
{
    private final Transfer<?> transfer;
    private final String      prefixName;
    
    public WrapperBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        transfer = TransferFactory.build(ckass);
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueNode, HttpServletResponse response)
    {
        ParamResolver node = treeValueNode.get(prefixName);
        if (node == null)
        {
            return null;
        }
        return transfer.trans(((StringValueResolver) node).getValue());
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
