package com.jfireframework.mvc.viewrender.impl;

import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.context.bean.annotation.field.PropertyRead;
import com.jfireframework.mvc.viewrender.DefaultResultType;
import com.jfireframework.mvc.viewrender.ViewRender;

@Resource
public class StringRender implements ViewRender
{
    @PropertyRead("encode")
    private String  encode = "UTF-8";
    private Charset charset;
    
    @PostConstruct
    public void init()
    {
        charset = Charset.forName(encode);
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.getOutputStream().write(((String) result).getBytes(charset));
    }
    
    @Override
    public String renderType()
    {
        return DefaultResultType.Str;
    }
}
