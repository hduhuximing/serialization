package com.jfireframework.mvc.binder.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class ListUploadBinder extends AbstractUploadBinder implements DataBinder
{
    private final String prefixName;
    
    public ListUploadBinder(String prefixName)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueNode, HttpServletResponse response)
    {
        String contentType = request.getContentType();
        if (StringUtil.isNotBlank(contentType) && contentType.startsWith("multipart/form-data"))
        {
            try
            {
                return resolveMany(request);
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
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
