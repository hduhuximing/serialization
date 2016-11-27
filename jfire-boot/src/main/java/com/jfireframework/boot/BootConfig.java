package com.jfireframework.boot;

import javax.servlet.Filter;
import io.undertow.server.handlers.resource.ResourceManager;

public class BootConfig
{
    private int                       port          = 80;
    private ResourceManager           resourceManager;
    private String                    appName;
    @SuppressWarnings("unchecked")
    private Class<? extends Filter>[] filterClasses = new Class[0];
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public ResourceManager getResourceManager()
    {
        return resourceManager;
    }
    
    public void setResourceManager(ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }
    
    public String getAppName()
    {
        return appName;
    }
    
    public void setAppName(String appName)
    {
        this.appName = appName;
    }
    
    public Class<? extends Filter>[] getFilterClasses()
    {
        return filterClasses;
    }
    
    public void setFilterClasses(Class<? extends Filter>[] filterClasses)
    {
        this.filterClasses = filterClasses;
    }
    
}
