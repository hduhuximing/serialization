package com.jfireframework.mvc.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.SimpleHotswapClassLoader;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.config.MvcConfig;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.core.action.ActionCenter;
import com.jfireframework.mvc.core.action.ActionCenterBulder;
import com.jfireframework.mvc.util.FileChangeDetect;

public class DispathServletHelper
{
    /** Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish */
    public static final String      COMMON_DEFAULT_SERVLET_NAME    = "default";
    /** Default Servlet name used by Google App Engine */
    public static final String      GAE_DEFAULT_SERVLET_NAME       = "_ah_default";
    /** Default Servlet name used by Resin */
    public static final String      RESIN_DEFAULT_SERVLET_NAME     = "resin-file";
    /** Default Servlet name used by WebLogic */
    public static final String      WEBLOGIC_DEFAULT_SERVLET_NAME  = "FileServlet";
    /** Default Servlet name used by WebSphere */
    public static final String      WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";
    private static final Logger     logger                         = ConsoleLogFactory.getLogger();
    private ActionCenter            actionCenter;
    private final ServletConfig     servletConfig;
    private final ServletContext    servletContext;
    private final RequestDispatcher staticResourceDispatcher;
    private final PreHandler        preHandler;
    private final MvcConfig         mvcConfig;
    
    public DispathServletHelper(ServletConfig servletConfig)
    {
        this.servletConfig = servletConfig;
        servletContext = servletConfig.getServletContext();
        staticResourceDispatcher = getStaticResourceDispatcher();
        actionCenter = ActionCenterBulder.generate(Thread.currentThread().getContextClassLoader(), servletContext, servletConfig);
        mvcConfig = actionCenter.getMvcConfig();
        if (mvcConfig.isHotdev())
        {
            preHandler = new HotswapPreHandler(mvcConfig);
        }
        else
        {
            preHandler = new NopPreHandler();
        }
        
    }
    
    private RequestDispatcher getStaticResourceDispatcher()
    {
        RequestDispatcher requestDispatcher = null;
        if ((requestDispatcher = servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME)) != null)
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
    
    public MvcConfig getMvcConfig()
    {
        return mvcConfig;
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
        private final ExecutorService  closeExector = Executors.newCachedThreadPool();
        
        public HotswapPreHandler(MvcConfig mvcConfig)
        {
            List<File> roots = new LinkedList<File>();
            for (String each : mvcConfig.getMonitorPath().split(","))
            {
                roots.add(new File(each));
            }
            detect = new FileChangeDetect(roots.toArray(new File[roots.size()]));
            reloadPath = mvcConfig.getReloadPath();
            reloadPackages = mvcConfig.getReloadPackages();
            if (StringUtil.isNotBlank(mvcConfig.getExcludePackages()))
            {
                excludePackages = mvcConfig.getExcludePackages();
            }
            else
            {
                excludePackages = null;
            }
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
                closeActionCenter(actionCenter);
                actionCenter = ActionCenterBulder.generate(classLoader, servletContext, servletConfig);
                logger.debug("热部署,耗时:{}", System.currentTimeMillis() - t0);
            }
        }
        
        private void closeActionCenter(final ActionCenter actionCenter)
        {
            closeExector.submit(new Runnable() {
                
                @Override
                public void run()
                {
                    actionCenter.close();
                }
            });
        }
    }
}
