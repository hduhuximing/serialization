package com.jfireframework.jfire.bean;

import java.util.Map;

public interface Bean extends BeanBootstrap
{
    
    public Object getInstance();
    
    /**
     * field进行属性注入的时候使用这个方法，这样如果需要循环引用，则因为大家都在一个map中，可以避免循环引用无限循环
     * 
     * @param beanInstanceMap
     * @return
     */
    public Object getInstance(Map<String, Object> beanInstanceMap);
    
}
