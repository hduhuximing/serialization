package com.jfireframework.context.event;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public interface EventPoster
{
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler);
}
