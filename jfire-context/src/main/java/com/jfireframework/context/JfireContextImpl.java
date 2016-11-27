package com.jfireframework.context;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.context.bean.Bean;

public class JfireContextImpl extends JfireContextBootstrapImpl implements JfireContext
{
    
    public JfireContextImpl()
    {
    }
    
    public JfireContextImpl(String... packageNames)
    {
        addPackageNames(packageNames);
    }
    
    @Override
    public Object getBean(String name)
    {
        if (init == false)
        {
            initContext();
        }
        Bean bean = beanNameMap.get(name);
        if (bean != null)
        {
            return bean.getInstance();
        }
        else
        {
            throw new UnSupportException("bean:" + name + "不存在");
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> src)
    {
        if (init == false)
        {
            initContext();
        }
        Bean bean = getBeanInfo(src);
        return (T) bean.getInstance();
    }
    
    @Override
    public Bean getBeanInfo(Class<?> beanClass)
    {
        if (init == false)
        {
            initContext();
        }
        Bean bean = beanTypeMap.get(beanClass);
        if (bean != null)
        {
            return bean;
        }
        throw new UnSupportException("bean" + beanClass.getName() + "不存在");
    }
    
    @Override
    public Bean getBeanInfo(String resName)
    {
        if (init == false)
        {
            initContext();
        }
        return beanNameMap.get(resName);
    }
    
    @Override
    public Bean[] getBeanByAnnotation(Class<? extends Annotation> annotationType)
    {
        if (init == false)
        {
            initContext();
        }
        List<Bean> beans = new LinkedList<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (AnnotationUtil.isPresent(annotationType, each.getOriginType()))
            {
                beans.add(each);
            }
        }
        return beans.toArray(new Bean[beans.size()]);
    }
    
    @Override
    public Bean[] getBeanByInterface(Class<?> type)
    {
        if (init == false)
        {
            initContext();
        }
        List<Bean> list = new LinkedList<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (type.isAssignableFrom(each.getOriginType()))
            {
                list.add(each);
            }
        }
        return list.toArray(new Bean[list.size()]);
    }
    
}
