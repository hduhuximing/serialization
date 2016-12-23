package com.jfireframework.mvc.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.SimpleHotswapClassLoader;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.config.MvcStaticConfig;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.core.action.ActionCenter;
import com.jfireframework.mvc.core.action.ActionCenterBulder;
import com.jfireframework.mvc.util.FileChangeDetect;

public class DispathServletHelper
{
    private static final Logger     logger          = ConsoleLogFactory.getLogger();
    private ActionCenter            actionCenter;
    private final ServletContext    servletContext;
    private final RequestDispatcher staticResourceDispatcher;
    private static final String     hotswapFileName = "hotswap.properties";
    private final PreHandler        preHandler;
    
    public DispathServletHelper(ServletContext servletContext)
    {
        this.servletContext = servletContext;
        staticResourceDispatcher = getStaticResourceDispatcher();
        if (DispathServletHelper.class.getClassLoader().getResource(hotswapFileName) != null)
        {
            InputStream inputStream = null;
            try
            {
                inputStream = DispathServletHelper.class.getClassLoader().getResourceAsStream(hotswapFileName);
                Properties properties = new Properties();
                properties.load(inputStream);
                if ("true".equals(properties.getProperty("hotswap")))
                {
                    preHandler = new HotswapPreHandler(properties);
                    preHandler.preHandle();
                }
                else
                {
                    preHandler = new NopPreHandler();
                    actionCenter = ActionCenterBulder.generate(Thread.currentThread().getContextClassLoader(), servletContext);
                }
            }
            catch (IOException e)
            {
                throw new JustThrowException(e);
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        throw new JustThrowException(e);
                    }
                }
            }
            
        }
        else
        {
            preHandler = new NopPreHandler();
            actionCenter = ActionCenterBulder.generate(Thread.currentThread().getContextClassLoader(), servletContext);
        }
        
    }
    
    private RequestDispatcher getStaticResourceDispatcher()
    {
        RequestDispatcher requestDispatcher = null;
        if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.COMMON_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.RESIN_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.WEBLOGIC_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.WEBSPHERE_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else
        {
            throw new UnSupportException("找不到默认用来处理静态资源的处理器");
        }
        return requestDispatcher;
    }
    
    public Action getAction(HttpServletRequest request)
    {
        return actionCenter.getAction(request);
    }
    
    public void handleStaticResourceRequest(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            staticResourceDispatcher.forward(request, response);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public void preHandle()
    {
        preHandler.preHandle();
    }
    
    interface PreHandler
    {
        public void preHandle();
    }
    
    class NopPreHandler implements PreHandler
    {
        
        @Override
        public void preHandle()
        {
        }
        
    }
    
    class HotswapPreHandler implements PreHandler
    {
        private final FileChangeDetect detect;
        private final String           reloadPath;
        private final String           reloadPackages;
        private final String           excludePackages;
        
        public HotswapPreHandler(Properties hotswapProperties)
        {
            String monitorPath = hotswapProperties.getProperty("monitorPath");
            List<File> roots = new LinkedList<File>();
            for (String each : monitorPath.split(","))
            {
                roots.add(new File(each));
            }
            detect = new FileChangeDetect(roots.toArray(new File[roots.size()]));
            reloadPath = hotswapProperties.getProperty("reloadPath");
            reloadPackages = hotswapProperties.getProperty("reloadPackages");
            excludePackages = hotswapProperties.getProperty("excludePackages");
        }
        
        @Override
        public void preHandle()
        {
            if (detect.detectChange())
            {
                long t0 = System.currentTimeMillis();
                SimpleHotswapClassLoader classLoader = new SimpleHotswapClassLoader(reloadPath);
                classLoader.setReloadPackages(reloadPackages.split(","));
                if (excludePackages != null)
                {
                    classLoader.setExcludePackages(excludePackages.split(","));
                }
                actionCenter = ActionCenterBulder.generate(classLoader, servletContext);
                logger.debug("热部署,耗时:{}", System.currentTimeMillis() - t0);
            }
        }
        
    }
}
