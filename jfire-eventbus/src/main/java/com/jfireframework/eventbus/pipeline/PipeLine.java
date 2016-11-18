package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;

public interface PipeLine extends EventContext<Object>
{
    public void add(Object data, Enum<? extends EventConfig> event, Object rowkey);
    
    public void add(Object data, Enum<? extends EventConfig> event);
    
    /**
     * 开始投递管道中的事件
     */
    public void start();
}
