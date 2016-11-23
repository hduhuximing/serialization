package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public interface EventBus
{
    
    public void addWorker();
    
    public void reduceWorker();
    
    public void stop();
    
    public void post(EventContext<?> eventContext);
    
    public Pipeline pipeline();
    
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler);
}
