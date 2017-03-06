package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.annotation.MvcIgnore;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.field.BinderField;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class ObjectDataBinder implements DataBinder
{
    private final String        prefixName;
    private final BinderField[] fields;
    private final Class<?>      ckass;
    private final static Logger LOGGER = ConsoleLogFactory.getLogger();
    
    public ObjectDataBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.ckass = ckass;
        this.prefixName = prefixName;
        Field[] t_fFields = ReflectUtil.getAllFields(ckass);
        LinkedList<BinderField> list = new LinkedList<BinderField>();
        for (Field each : t_fFields)
        {
            if (Modifier.isStatic(each.getModifiers()) || Modifier.isFinal(each.getModifiers()) || each.isAnnotationPresent(MvcIgnore.class))
            {
                continue;
            }
            list.add(AbstractBinderField.build(each));
        }
        fields = list.toArray(new BinderField[list.size()]);
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueResolver, HttpServletResponse response)
    {
        if (prefixName.length() != 0)
        {
            ParamResolver resolver = treeValueResolver.get(prefixName);
            if (resolver instanceof TreeValueResolver)
            {
                treeValueResolver = (TreeValueResolver) resolver;
            }
            else
            {
                throw new UnsupportedOperationException("尝试获取的数据是一组键值对，但是获取的数据类型是" + resolver.getClass());
            }
        }
        try
        {
            if (treeValueResolver == null)
            {
                LOGGER.debug("尝试获取对象{}的数据，但是不存在", prefixName);
                return null;
            }
            Object entity = null;
            for (BinderField each : fields)
            {
                ParamResolver node = treeValueResolver.get(each.getName());
                if (node != null)
                {
                    if (entity == null)
                    {
                        entity = ckass.newInstance();
                    }
                    try
                    {
                        each.setValue(request, response, node, entity);
                    }
                    catch (Exception e)
                    {
                        throw new JustThrowException(StringUtil.format("参数:{}绑定出现异常", each.getName()), e);
                    }
                }
            }
            return entity;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
