package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public interface Operator
{
    public EventHandler<?> handler();
    
    public ParallelLevel level();
    
    public EventExecutor executor();
    
    public Object eventData();
    
    public void work(Object data, Pipeline pipeline);
    
    public Object rowKey();
}
