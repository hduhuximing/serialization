package com.jfireframework.context.bean;

import com.jfireframework.context.config.BeanInfo;
import com.jfireframework.context.config.Profile;

public interface JfireConfiger
{
    public BeanInfo[] infos();
    
    public Profile[] profiles();
}
