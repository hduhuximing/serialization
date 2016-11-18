package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;

public interface Pipeline extends EventContext<Object>
{
    public void add(Object data, Enum<? extends EventConfig> event, Object rowkey);
    
    public void add(Object data, Enum<? extends EventConfig> event);
    
    public void add(Enum<? extends EventConfig> event, Object rowkey);
    
    public void add(Enum<? extends EventConfig> event);
    
    /**
     * 管道开始投递
     */
    public void start();
}
