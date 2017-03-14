package com.jfireframework.mvc.util.test;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MockServletConfig implements ServletConfig
{
    private final ServletContext      servletContext = new MockServletContext();
    private final Map<String, String> params;
    
    public MockServletConfig(Map<String, String> params)
    {
        this.params = params;
    }
    
    @Override
    public String getServletName()
    {
        return "mock";
    }
    
    @Override
    public ServletContext getServletContext()
    {
        return servletContext;
    }
    
    @Override
    public String getInitParameter(String name)
    {
        return params.get(name);
    }
    
    @Override
    public Enumeration<String> getInitParameterNames()
    {
        Hashtable<String, String> hashtable = new Hashtable<String, String>(params);
        return hashtable.keys();
    }
    
}
