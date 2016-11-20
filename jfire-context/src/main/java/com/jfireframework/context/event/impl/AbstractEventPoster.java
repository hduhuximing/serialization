package com.jfireframework.context.event.impl;

import java.util.Set;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public abstract class AbstractEventPoster implements EventPoster
{
    protected Set<Class<? extends Enum<? extends EventConfig>>> events;
    protected EventBus                                          eventBus;
    
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        return eventBus.post(data, event, rowkey, handler);
    }
    
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        return eventBus.post(data, event, handler);
    }
    
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        return eventBus.syncPost(data, event, rowkey, handler);
    }
    
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        return eventBus.syncPost(data, event, handler);
    }
}
