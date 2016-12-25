package com.jfireframework.boot;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebFilter;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.core.EasyMvcDispathServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;

public class BootStarter
{
    private final int                       port;
    private final String                    appName;
    private final ResourceManager           resourceManager;
    private final Class<? extends Filter>[] filterClasses;
    
    public BootStarter(BootConfig config)
    {
        port = config.getPort();
        appName = config.getAppName().startsWith("//") ? config.getAppName() : '/' + config.getAppName();
        filterClasses = config.getFilterClasses();
        resourceManager = config.getResourceManager();
    }
    
    public void start()
    {
        try
        {
            DeploymentInfo servletBuilder = Servlets.deployment()//
                    .setClassLoader(BootStarter.class.getClassLoader())//
                    .setContextPath(appName)//
                    .setDeploymentName("bootstarter")//
                    .addServlets(
                            //
                            Servlets.servlet("EasyMvcDispathServlet", EasyMvcDispathServlet.class)//
                                    .addMapping("/*")//
                                    .setEnabled(true)//
                                    .setLoadOnStartup(1)//
                                    .setAsyncSupported(true)//
                                    .setMultipartConfig(new MultipartConfigElement(EasyMvcDispathServlet.class.getAnnotation(MultipartConfig.class)))
                    );
            servletBuilder.setResourceManager(resourceManager);
            for (Class<? extends Filter> each : filterClasses)
            {
                if (each.isAnnotationPresent(WebFilter.class))
                {
                    WebFilter webFilter = each.getAnnotation(WebFilter.class);
                    FilterInfo filterInfo = new FilterInfo(webFilter.filterName(), each);
                    filterInfo.setAsyncSupported(webFilter.asyncSupported());
                    servletBuilder.addFilter(filterInfo);
                    for (String url : webFilter.value())
                    {
                        servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.FORWARD);
                        servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.INCLUDE);
                        servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.REQUEST);
                    }
                }
            }
            DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
            manager.deploy();
            PathHandler path = Handlers.path(Handlers.redirect(appName)).addPrefixPath(appName, manager.start());
            Undertow server = Undertow.builder().addHttpListener(port, "0.0.0.0").setHandler(path).build();
            server.start();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
}
