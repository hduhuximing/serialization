package com.jfireframework.mvc.viewrender.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.viewrender.DefaultResultType;
import com.jfireframework.mvc.viewrender.ViewRender;

@Resource
public class HtmlRender implements ViewRender
{
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        request.getRequestDispatcher((String) result).forward(request, response);
    }
    
    @Override
    public String renderType()
    {
        return DefaultResultType.Html;
    }
}
