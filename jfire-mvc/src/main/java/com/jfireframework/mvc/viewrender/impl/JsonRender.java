package com.jfireframework.mvc.viewrender.impl;

import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.jfire.bean.annotation.field.PropertyRead;
import com.jfireframework.mvc.viewrender.DefaultResultType;
import com.jfireframework.mvc.viewrender.ViewRender;

@Resource
public class JsonRender implements ViewRender
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
        OutputStream out = response.getOutputStream();
        out.write(JsonTool.write(result).getBytes(charset));
    }
    
    @Override
    public String renderType()
    {
        return DefaultResultType.Json;
    }
}
