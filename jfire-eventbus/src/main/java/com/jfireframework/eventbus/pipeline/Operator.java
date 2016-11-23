package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public interface Operator
{
    public EventHandler<?> handler();
    
    public ParallelLevel level();
    
    public EventExecutor executor();
    
    public Object eventData();
    
    public void work(EventContext<?> eventContext, EventBus eventBus);
    
    public Object rowKey();
}
