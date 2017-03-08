package com.jfireframework.swaggertool.ui;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SwaggerCorsFilter implements Filter
{
    
    @Override
    public void destroy()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;
        response.addHeader("Access-Control-Allow-Origin", "*");
        String method = request.getMethod();
        if (method.equals("OPTIONS"))
        {
            String accessHeaders = request.getHeader("Access-Control-Request-Headers");
            if (accessHeaders != null)
            {
                // 如果存在自定义的header参数，需要在此处添加，逗号分隔
                response.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, " + "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, " + "Content-Type, X-E4M-With," + accessHeaders);
            }
            else
            {
                response.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, " + "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, " + "Content-Type, X-E4M-With");
            }
            response.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.flushBuffer();
        }
        else
        {
            arg2.doFilter(arg0, arg1);
        }
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException
    {
        // TODO Auto-generated method stub
        
    }
    
}
