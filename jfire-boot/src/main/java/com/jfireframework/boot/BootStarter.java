package com.jfireframework.boot;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebFilter;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.jfire.bean.annotation.field.PropertyRead;
import com.jfireframework.mvc.core.EasyMvcDispathServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ServletInfo;

public class BootStarter
{
    @PropertyRead("boot_port")
    private int          port;
    @PropertyRead("boot_appName")
    private String       appName;
    @PropertyRead("boot_configClassName")
    private String       configClassName;
    @PropertyRead("boot_packageName")
    private String       packageName;
    @PropertyRead("jfire.mvc.classpathPrefix")
    private String       classpathPrefix = "web";
    @PropertyRead("jfire.mvc.mode")
    private String       mode            = "run_in_jar_mode";
    @Resource
    private List<Filter> filters   = new LinkedList<>();
    
    @PostConstruct
    public void init()
    {
        appName = appName.startsWith("//") ? appName : '/' + appName;
    }
    
    public void start()
    {
        try
        {
            ServletInfo servletInfo = Servlets.servlet("EasyMvcDispathServlet", EasyMvcDispathServlet.class)//
                    .addMapping("/*")//
                    .setEnabled(true)//
                    .setLoadOnStartup(1)//
                    .setAsyncSupported(true)//
                    .setMultipartConfig(new MultipartConfigElement(EasyMvcDispathServlet.class.getAnnotation(MultipartConfig.class)))//
                    .addInitParam(EasyMvcDispathServlet.CONFIG_CLASS_NAME, configClassName)//
                    .addInitParam(EasyMvcDispathServlet.SACAN_PACKAGENAME, packageName)//
                    .addInitParam("jfire.mvc.mode", mode)//
                    .addInitParam("jfire.mvc.classpathPrefix", classpathPrefix);
            DeploymentInfo servletBuilder = Servlets.deployment()//
                    .setClassLoader(BootStarter.class.getClassLoader())//
                    .setContextPath(appName)//
                    .setDeploymentName("bootstarter")//
                    .addServlets(servletInfo);
            servletBuilder.setResourceManager(new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), classpathPrefix));
            for (Filter filter : filters)
            {
                Class<? extends Filter> ckass = filter.getClass();
                if (ckass.isAnnotationPresent(WebFilter.class) == false)
                {
                    continue;
                }
                WebFilter webFilter = ckass.getAnnotation(WebFilter.class);
                FilterInfo filterInfo = new FilterInfo(webFilter.filterName(), ckass);
                filterInfo.setAsyncSupported(webFilter.asyncSupported());
                servletBuilder.addFilter(filterInfo);
                for (String url : webFilter.value())
                {
                    servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.FORWARD);
                    servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.INCLUDE);
                    servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.REQUEST);
                    servletBuilder.addFilterUrlMapping(webFilter.filterName(), url, DispatcherType.ASYNC);
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
