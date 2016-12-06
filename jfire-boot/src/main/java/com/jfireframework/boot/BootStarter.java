package com.jfireframework.boot;

import java.net.Inet4Address;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
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
                                    .setEnabled(true)
                    );
            servletBuilder.setResourceManager(resourceManager);
            for (Class<? extends Filter> each : filterClasses)
            {
                if (each.isAnnotationPresent(WebFilter.class))
                {
                    WebFilter webFilter = each.getAnnotation(WebFilter.class);
                    FilterInfo filterInfo = new FilterInfo(webFilter.filterName(), each);
                    servletBuilder.addFilter(filterInfo);
                    for (String url : webFilter.value())
                    {
                        servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.FORWARD);
                    }
                }
            }
            DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
            manager.deploy();
            PathHandler path = Handlers.path(Handlers.redirect(appName)).addPrefixPath(appName, manager.start());
            Undertow server = Undertow.builder().addHttpListener(port, Inet4Address.getLocalHost().getHostAddress()).addHttpListener(port, "localhost").setHandler(path).build();
            server.start();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
}
