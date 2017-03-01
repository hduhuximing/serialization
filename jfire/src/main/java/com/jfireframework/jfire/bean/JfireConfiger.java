package com.jfireframework.jfire.bean;

import com.jfireframework.jfire.config.BeanInfo;
import com.jfireframework.jfire.config.Profile;

public interface JfireConfiger
{
    public BeanInfo[] infos();
    
    public Profile[] profiles();
}
