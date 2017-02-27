package com.jfireframework.mvc.core.action;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;
import com.jfireframework.mvc.config.RequestMethod;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rule.HeaderRule;
import com.jfireframework.mvc.rule.RestfulRule;
import com.jfireframework.mvc.viewrender.ViewRender;
import sun.reflect.MethodAccessor;

/**
 * 传统action类，用来代表一个事先定义的url地址响应，该url地址中不包含*这样的通配符
 * 
 * 
 */
public class Action
{
    /** 调用该action的对象实例 */
    private final Object                            actionEntity;
    private final DataBinder[]                      dataBinders;
    // 该action方法的快速反射调用工具
    private final MethodAccessor                    methodAccessor;
    // 该action响应的url地址
    private final String                            requestUrl;
    private final boolean                           rest;
    private final RestfulRule                       restfulRule;
    private final boolean                           readStream;
    private final RequestMethod                     requestMethod;
    private final Method                            method;
    private final String                            contentType;
    private final ActionInterceptor[]               interceptors;
    private final String                            token;
    private final ViewRender                        viewRender;
    private final HeaderRule                        headerRule;
    private final boolean                           hasCookie;
    private final boolean                           hasHeader;
    private final int[]                             validatorIndexs;
    private static final ThreadLocal<TreeValueResolver> threadBindResolver = new ThreadLocal<TreeValueResolver>() {
                                                                       protected TreeValueResolver initialValue()
                                                                       {
                                                                           return new TreeValueResolver();
                                                                       }
                                                                   };
    
    public Action(ActionInfo info)
    {
        headerRule = info.getHeaderRule();
        viewRender = info.getViewRender();
        actionEntity = info.getEntity();
        dataBinders = info.getDataBinders();
        methodAccessor = info.getMethodAccessor();
        requestUrl = info.getRequestUrl();
        rest = info.isRest();
        restfulRule = info.getRestfulRule();
        readStream = info.isReadStream();
        requestMethod = info.getRequestMethod();
        method = info.getMethod();
        token = info.getToken();
        hasCookie = info.isHasCookie();
        hasHeader = info.isHasCookie();
        contentType = info.getContentType();
        interceptors = info.getInterceptors();
        validatorIndexs = info.getValidatorIndex();
    }
    
    public void render(HttpServletRequest request, HttpServletResponse response)
    {
        TreeValueResolver resolver = threadBindResolver.get();
        resolver.clear();
        for (ActionInterceptor each : interceptors)
        {
            if (each.interceptor(request, response, this, resolver) == false)
            {
                return;
            }
        }
        try
        {
            if (contentType != null)
            {
                response.setContentType(contentType);
            }
            viewRender.render(request, response, methodAccessor.invoke(actionEntity, resolveParams(request, response, resolver)));
        }
        catch (Throwable e)
        {
            throw new JustThrowException(e);
        }
    }
    
    private Object[] resolveParams(HttpServletRequest request, HttpServletResponse response, TreeValueResolver node)
    {
        if (readStream == false)
        {
            for (Entry<String, String[]> each : request.getParameterMap().entrySet())
            {
                for (String value : each.getValue())
                {
                    if (value.equals("") == false)
                    {
                        node.put(each.getKey(), value);
                    }
                }
            }
        }
        if (isRest())
        {
            restfulRule.getObtain(request.getRequestURI(), node);
        }
        int length = dataBinders.length;
        Object[] param = new Object[length];
        for (int i = 0; i < length; i++)
        {
            try
            {
                param[i] = dataBinders[i].bind(request, node, response);
            }
            catch (Exception e)
            {
                throw new JustThrowException(StringUtil.format("参数：{}绑定出现异常", dataBinders[i].getParamName()), e);
            }
        }
        return param;
    }
    
    public Object getActionEntity()
    {
        return actionEntity;
    }
    
    public DataBinder[] getDataBinders()
    {
        return dataBinders;
    }
    
    public MethodAccessor getMethodAccessor()
    {
        return methodAccessor;
    }
    
    public String getRequestUrl()
    {
        return requestUrl;
    }
    
    public boolean isRest()
    {
        return rest;
    }
    
    public RestfulRule getRestfulRule()
    {
        return restfulRule;
    }
    
    public boolean isReadStream()
    {
        return readStream;
    }
    
    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }
    
    public Method getMethod()
    {
        return method;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public ActionInterceptor[] getInterceptors()
    {
        return interceptors;
    }
    
    public ViewRender getViewRender()
    {
        return viewRender;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public HeaderRule getHeaderRule()
    {
        return headerRule;
    }
    
    @Override
    public int hashCode()
    {
        return 0;
    }
    
    @Override
    public boolean equals(Object target)
    {
        if (target instanceof Action)
        {
            Action tmp = (Action) target;
            if (tmp.getRequestUrl().equals(requestUrl) && tmp.getHeaderRule().equals(headerRule))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    public boolean hasCookie()
    {
        return hasCookie;
    }
    
    public boolean hasHeader()
    {
        return hasHeader;
    }
    
    public int[] getValidatorIndexs()
    {
        return validatorIndexs;
    }
    
}
