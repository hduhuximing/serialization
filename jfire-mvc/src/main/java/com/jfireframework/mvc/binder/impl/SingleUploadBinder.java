package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class SingleUploadBinder extends AbstractUploadBinder implements DataBinder
{
    private final String        prefixName;
    private static final String EMPTY = "";
    
    public SingleUploadBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        if (prefixName.equals(""))
        {
            this.prefixName = EMPTY;
        }
        else
        {
            this.prefixName = prefixName;
        }
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueNode, HttpServletResponse response)
    {
        String contentType = request.getContentType();
        if (StringUtil.isNotBlank(contentType) && contentType.startsWith("multipart/form-data"))
        {
            if (prefixName == EMPTY)
            {
                try
                {
                    return resolveOne(request);
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
            }
            else
            {
                try
                {
                    return resolveOne(prefixName, request);
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
        else
        {
            return null;
        }
        
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
