package com.jfireframework.boot;

import java.io.File;
import javax.servlet.Filter;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;

public class BootConfigBuilder
{
    public static BootConfig newFileMavenEnv(String appName, Class<? extends Filter>... filterClasses)
    {
        return newFileMavenEnv(appName, 80, filterClasses);
    }
    
    @SuppressWarnings("unchecked")
    public static BootConfig newFileMavenEnv(String appName)
    {
        return newFileMavenEnv(appName, new Class[0]);
    }
    
    public static BootConfig newFileMavenEnv(String appName, int port, Class<? extends Filter>... filterClasses)
    {
        BootConfig config = new BootConfig();
        config.setPort(port);
        ResourceManager resourceManager = new FileResourceManager(new File("src/main/webapp"), 1024 * 512);
        config.setResourceManager(resourceManager);
        config.setAppName(appName);
        config.setFilterClasses(filterClasses);
        return config;
    }
    
    public static BootConfig newClasspathMavenEnv(String appName, String classpathPrefix, int port, Class<? extends Filter>... filterClasses)
    {
        BootConfig config = new BootConfig();
        config.setPort(port);
        ResourceManager resourceManager = new ClassPathResourceManager(BootConfig.class.getClassLoader(), classpathPrefix);
        config.setResourceManager(resourceManager);
        config.setAppName(appName);
        config.setFilterClasses(filterClasses);
        return config;
    }
    
    @SuppressWarnings("unchecked")
    public static BootConfig newClasspathMavenEnv(String appName, String classpathPrefix, int port)
    {
        return newClasspathMavenEnv(appName, classpathPrefix, port, new Class[0]);
    }
    
    public static BootConfig newClasspathMavenEnv(String appName, String classpathPrefix)
    {
        return newClasspathMavenEnv(appName, classpathPrefix, 80);
    }
    
    public static BootConfig newClasspathMavenEnv(String appName)
    {
        return newClasspathMavenEnv(appName, "", 80);
    }
}
