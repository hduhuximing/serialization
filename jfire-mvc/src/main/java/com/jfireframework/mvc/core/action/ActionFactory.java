package com.jfireframework.mvc.core.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.uniqueid.SummerId;
import com.jfireframework.baseutil.uniqueid.Uid;
import com.jfireframework.jfire.Jfire;
import com.jfireframework.jfire.bean.Bean;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.annotation.RequestParam;
import com.jfireframework.mvc.binder.BinderFactory;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.impl.CookieBinder;
import com.jfireframework.mvc.binder.impl.HeaderBinder;
import com.jfireframework.mvc.binder.impl.HttpServletRequestBinder;
import com.jfireframework.mvc.binder.impl.HttpServletResponseBinder;
import com.jfireframework.mvc.binder.impl.HttpSessionBinder;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rule.RestfulRule;
import com.jfireframework.mvc.util.ContentType;
import com.jfireframework.mvc.viewrender.DefaultResultType;
import com.jfireframework.mvc.viewrender.RenderManager;

public class ActionFactory
{
    
    private static final AescComparator AESC_COMPARATOR = new AescComparator();
    private static final Uid            uid             = new SummerId(0);
    
    /**
     * 使用方法对象，顶级请求路径，容器对象初始化一个action实例。 该实例负责该action的调用
     * 
     * @param method
     * @param rootRequestPath 顶级请求路径，实际的请求路径为顶级请求路径/方法请求路径
     * @param beanContext
     */
    public static Action buildAction(Method method, String requestPath, Bean bean, Jfire jfire)
    {
        RenderManager renderManager = jfire.getBean(RenderManager.class);
        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setMethod(method);
        RequestMapping requestMapping = AnnotationUtil.getAnnotation(RequestMapping.class, method);
        actionInfo.setRequestMethod(requestMapping.method());
        actionInfo.setDataBinders(generateBinders(method, bean));
        actionInfo.setReadStream(requestMapping.readStream());
        actionInfo.setEntity(bean.getInstance());
        actionInfo.setHeaders(requestMapping.headers());
        String resultType = "";
        if (requestMapping.resultType() == DefaultResultType.None && method.getReturnType() != Void.class)
        {
            Class<?> returnType = method.getReturnType();
            if (returnType == String.class)
            {
                actionInfo.setViewRender(renderManager.get(DefaultResultType.Str, method));
                resultType = DefaultResultType.Str;
            }
            else if (returnType == ModelAndView.class)
            {
                actionInfo.setViewRender(renderManager.get(DefaultResultType.Beetl, method));
                resultType = DefaultResultType.Beetl;
            }
            else if (returnType == byte[].class)
            {
                actionInfo.setViewRender(renderManager.get(DefaultResultType.Bytes, method));
                resultType = DefaultResultType.Bytes;
            }
            else
            {
                actionInfo.setViewRender(renderManager.get(DefaultResultType.Json, method));
                resultType = DefaultResultType.Json;
            }
        }
        else
        {
            actionInfo.setViewRender(renderManager.get(requestMapping.resultType(), method));
            resultType = requestMapping.resultType();
        }
        if (requestMapping.contentType().equals(""))
        {
            if (resultType.equals(DefaultResultType.Json))
            {
                actionInfo.setContentType(ContentType.JSON);
            }
            else if (resultType.equals(DefaultResultType.Html))
            {
                actionInfo.setContentType(ContentType.HTML);
            }
            else if (resultType.equals(DefaultResultType.Str))
            {
                actionInfo.setContentType(ContentType.STRING);
            }
            else if (resultType.equals(DefaultResultType.Bytes))
            {
                actionInfo.setContentType(ContentType.STREAM);
            }
            else if (resultType.equals(DefaultResultType.Beetl))
            {
                actionInfo.setContentType(ContentType.HTML);
            }
        }
        else
        {
            actionInfo.setContentType(requestMapping.contentType());
        }
        if ("".equals(actionInfo.getContentType()))
        {
            actionInfo.setContentType(null);
        }
        actionInfo.setToken(requestMapping.token());
        if (actionInfo.getToken().equals(""))
        {
            actionInfo.setToken(uid.generate());
        }
        actionInfo.setRequestUrl(getRequestUrl(requestMapping, actionInfo, requestPath, method));
        try
        {
            // 使用原始方法的名称和参数类型数组,在类型中获取真实的方法。这一步主要是防止action类本身被增强后，却仍然调用未增强的方法。
            Method realMethod = bean.getType().getMethod(method.getName(), method.getParameterTypes());
            actionInfo.setMethodAccessor(ReflectUtil.fastMethod(realMethod));
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        actionInfo.setInterceptors(getInterceptors(jfire, actionInfo));
        return new Action(actionInfo);
    }
    
    private static ActionInterceptor[] getInterceptors(Jfire jfire, ActionInfo info)
    {
        Bean[] beans = jfire.getBeanByInterface(ActionInterceptor.class);
        List<ActionInterceptor> interceptors = new ArrayList<ActionInterceptor>();
        next: for (Bean each : beans)
        {
            ActionInterceptor interceptor = (ActionInterceptor) each.getInstance();
            String excludePath = interceptor.excludePath();
            if (StringUtil.isNotBlank(excludePath))
            {
                if ("*".equals(excludePath))
                {
                    continue next;
                }
                else
                {
                    for (String singleExRule : excludePath.split(";"))
                    {
                        if (isInterceptored(info.getRequestUrl(), singleExRule))
                        {
                            continue next;
                        }
                    }
                }
            }
            String includePath = interceptor.includePath();
            if (StringUtil.isNotBlank(includePath))
            {
                if ("*".equals(includePath))
                {
                    interceptors.add(interceptor);
                    continue next;
                }
                else
                {
                    for (String singleInRule : includePath.split(";"))
                    {
                        if (isInterceptored(info.getRequestUrl(), singleInRule))
                        {
                            interceptors.add(interceptor);
                            continue next;
                        }
                    }
                }
            }
            String token = interceptor.tokenRule();
            if (StringUtil.isNotBlank(token) && token.equals(info.getToken()))
            {
                interceptors.add(interceptor);
            }
            
        }
        Collections.sort(interceptors, AESC_COMPARATOR);
        return interceptors.toArray(new ActionInterceptor[interceptors.size()]);
    }
    
    private static String getRequestUrl(RequestMapping requestMapping, ActionInfo actionInfo, String requestPath, Method method)
    {
        if (requestMapping.value().equals("/"))
        {
            ;
        }
        else
        {
            actionInfo.setRest(requestMapping.rest());
            if (requestMapping.value().indexOf("{") != -1 && requestMapping.value().indexOf("}") != -1)
            {
                actionInfo.setRest(true);
            }
            if (actionInfo.isRest())
            {
                if (StringUtil.isNotBlank(requestMapping.value()))
                {
                    requestPath += requestMapping.value();
                }
                else
                {
                    requestPath += "/" + method.getName();
                }
                if (requestPath.indexOf("{") == -1)
                {
                    for (DataBinder each : actionInfo.getDataBinders())
                    {
                        if (each instanceof HttpSessionBinder //
                                || each instanceof HttpServletRequestBinder //
                                || each instanceof HttpServletResponseBinder //
                                || each instanceof CookieBinder //
                                || each instanceof HeaderBinder)
                        {
                            continue;
                        }
                        requestPath += "/{" + each.getParamName() + "}";
                    }
                }
                actionInfo.setRestfulRule(new RestfulRule(requestPath));
            }
            else
            {
                requestPath += (StringUtil.isNotBlank(requestMapping.value()) ? requestMapping.value() : "/" + method.getName());
            }
        }
        return requestPath;
    }
    
    private static boolean isInterceptored(String requestPath, String rule)
    {
        String[] rules = rule.split("\\*");
        int index = 0;
        for (int i = 0; i < rules.length; i++)
        {
            index = requestPath.indexOf(rules[i], index);
            if (index < 0)
            {
                return false;
            }
            index += rules[i].length();
        }
        return true;
    }
    
    private static DataBinder[] generateBinders(Method method, Bean bean)
    {
        if (method.getParameterTypes().length == 0)
        {
            return new DataBinder[0];
        }
        Type[] paramTypes = method.getGenericParameterTypes();
        Class<?>[] ckasss = method.getParameterTypes();
        String[] paramNames = getParamNames(method, bean);
        Annotation[][] annotations = method.getParameterAnnotations();
        DataBinder[] dataBinders = BinderFactory.build(ckasss, paramTypes, paramNames, annotations);
        return dataBinders;
    }
    
    /**
     * 获取方法的参数名称数组，如果没有注解则使用形参名称，如果有，则该参数采用注解的名称
     * 
     * @param method
     * @return
     */
    private static String[] getParamNames(Method method, Bean bean)
    {
        String[] paramNames;
        try
        {
            paramNames = bean.getMethodParamNames(method);
        }
        catch (Exception e)
        {
            paramNames = new String[method.getParameterTypes().length];
        }
        Annotation[][] annos = method.getParameterAnnotations();
        for (int i = 0; i < annos.length; i++)
        {
            if (annos[i].length == 0)
            {
                continue;
            }
            else
            {
                for (Annotation each : annos[i])
                {
                    if (each instanceof RequestParam)
                    {
                        paramNames[i] = ((RequestParam) each).value();
                        break;
                    }
                }
            }
        }
        return paramNames;
    }
    
}
