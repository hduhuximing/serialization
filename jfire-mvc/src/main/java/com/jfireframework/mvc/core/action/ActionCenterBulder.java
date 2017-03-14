package com.jfireframework.mvc.core.action;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.jfire.Jfire;
import com.jfireframework.jfire.JfireConfig;
import com.jfireframework.jfire.bean.Bean;
import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.config.MvcConfig;
import com.jfireframework.mvc.core.EasyMvcDispathServlet;
import com.jfireframework.mvc.viewrender.RenderManager;
import com.jfireframework.mvc.viewrender.impl.BytesRender;
import com.jfireframework.mvc.viewrender.impl.HtmlRender;
import com.jfireframework.mvc.viewrender.impl.JsonRender;
import com.jfireframework.mvc.viewrender.impl.NoneRender;
import com.jfireframework.mvc.viewrender.impl.RedirectRender;
import com.jfireframework.mvc.viewrender.impl.StringRender;

public class ActionCenterBulder
{
    
    public static ActionCenter generate(ClassLoader classLoader, ServletContext servletContext, ServletConfig servletConfig)
    {
        JfireConfig jfireConfig = new JfireConfig();
        readConfig(jfireConfig, servletConfig, classLoader);
        jfireConfig.addSingletonEntity(classLoader.getClass().getName(), classLoader);
        jfireConfig.setClassLoader(classLoader);
        addViewRender(jfireConfig);
        jfireConfig.addSingletonEntity("servletContext", servletContext);
        jfireConfig.addBean(MvcConfig.class);
        Jfire jfire = new Jfire(jfireConfig);
        ActionCenter actionCenter = new ActionCenter(generateActions(servletContext.getContextPath(), jfire).toArray(new Action[0]), jfire);
        actionCenter.setMvcConfig(jfire.getBean(MvcConfig.class));
        return actionCenter;
    }
    
    private static void readConfig(JfireConfig jfireConfig, ServletConfig servletConfig, ClassLoader classLoader)
    {
        if (servletConfig.getInitParameter(EasyMvcDispathServlet.CONFIG_CLASS_NAME) != null)
        {
            String name = servletConfig.getInitParameter(EasyMvcDispathServlet.CONFIG_CLASS_NAME);
            try
            {
                Class<?> configClass = classLoader.loadClass(name);
                jfireConfig.readConfig(configClass);
            }
            catch (ClassNotFoundException e)
            {
                throw new JustThrowException(e);
            }
        }
        Properties properties = new Properties();
        Enumeration<String> initParams = servletConfig.getInitParameterNames();
        while (initParams.hasMoreElements())
        {
            String key = initParams.nextElement();
            properties.put(key, servletConfig.getInitParameter(key));
        }
        jfireConfig.addProperties(properties);
        if (classLoader.getResource("mvc.json") != null)
        {
            JsonObject config = (JsonObject) JsonTool.fromString(StringUtil.readFromClasspath("mvc.json", Charset.forName("utf8")));
            jfireConfig.readConfig(config);
        }
    }
    
    private static void addViewRender(JfireConfig jfireConfig)
    {
        {
            jfireConfig.addBean(JsonRender.class);
            jfireConfig.addBean(HtmlRender.class);
            jfireConfig.addBean(StringRender.class);
            jfireConfig.addBean(RedirectRender.class);
            jfireConfig.addBean(NoneRender.class);
            jfireConfig.addBean(BytesRender.class);
        }
        {
            jfireConfig.addBean(RenderManager.class);
        }
    }
    
    /**
     * 初始化Beancontext容器，并且抽取其中的ActionClass注解的类，将action实例化
     */
    private static List<Action> generateActions(String contextUrl, Jfire jfire)
    {
        Bean[] beans = jfire.getBeanByAnnotation(Controller.class);
        Bean[] listenerBeans = jfire.getBeanByInterface(ActionInitListener.class);
        List<ActionInitListener> tmp = new LinkedList<ActionInitListener>();
        for (Bean each : listenerBeans)
        {
            tmp.add((ActionInitListener) each.getInstance());
        }
        ActionInitListener[] listeners = tmp.toArray(new ActionInitListener[tmp.size()]);
        List<Action> list = new ArrayList<Action>();
        for (Bean each : beans)
        {
            list.addAll(generateActions(each, listeners, jfire, contextUrl));
        }
        for (ActionInitListener listener : listeners)
        {
            listener.initFinish();
        }
        return list;
    }
    
    /**
     * 创建某一个bean下面的所有action
     * 
     * @param bean
     * @param listeners
     * @param contextUrl
     * @param jfire
     * @return
     */
    private static List<Action> generateActions(Bean bean, ActionInitListener[] listeners, Jfire jfire, String contextUrl)
    {
        Class<?> src = bean.getOriginType();
        String requestUrl = contextUrl;
        if (AnnotationUtil.isPresent(RequestMapping.class, src))
        {
            RequestMapping requestMapping = AnnotationUtil.getAnnotation(RequestMapping.class, src);
            requestUrl += requestMapping.value();
        }
        // 这里需要使用原始的类来得到方法，因为如果使用增强后的子类，就无法得到正确的方法名称以及方法上的注解信息
        Method[] methods = ReflectUtil.getAllMehtods(bean.getOriginType());
        List<Action> list = new ArrayList<Action>();
        for (Method each : methods)
        {
            if (AnnotationUtil.isPresent(RequestMapping.class, each))
            {
                Action action = ActionFactory.buildAction(each, requestUrl, bean, jfire);
                list.add(action);
                for (ActionInitListener listener : listeners)
                {
                    listener.init(action);
                }
            }
        }
        return list;
    }
}
