package com.jfireframework.context;

import java.lang.annotation.Annotation;
import com.jfireframework.context.bean.Bean;

public interface JfireContext extends JfireContextBootstrap
{
    
    /**
     * 根据Bean名称获取对象实例
     * 
     * @param name
     * @return
     */
    public Object getBean(String name);
    
    /**
     * 根据给定的类型，查找bean后实例化并且返回
     * 
     * @param src
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public <T> T getBean(Class<T> src);
    
    /**
     * 根据给定的类型，查询当前类型匹配参数的bean返回
     * 
     * @param beanClass
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public Bean getBeanInfo(Class<?> beanClass);
    
    /**
     * 根据注解名称，查询Bean信息并且返回
     * 
     * @param resName
     * @return
     */
    public Bean getBeanInfo(String resName);
    
    /**
     * 查询容器之中类持有特定注解的bean数组
     * 
     * @param annotationType
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public Bean[] getBeanByAnnotation(Class<? extends Annotation> annotationType);
    
    /**
     * 查询容器之中类实现特定接口的bean数组
     * 
     * @param type
     * @return
     */
    public Bean[] getBeanByInterface(Class<?> type);
    
}
