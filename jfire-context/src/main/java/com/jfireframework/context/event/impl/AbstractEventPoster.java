package com.jfireframework.context.event.impl;

import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public abstract class AbstractEventPoster implements EventPoster
{
    protected String   eventPath;
    protected EventBus eventBus;
    
    @SuppressWarnings("unchecked")
    protected void register()
    {
        String[] events = PackageScan.scan(eventPath);
        for (String each : events)
        {
            try
            {
                Class<?> ckass = Class.forName(each);
                if (Enum.class.isAssignableFrom(ckass) || EventConfig.class.isAssignableFrom(ckass))
                {
                    eventBus.register((Class<? extends Enum<? extends EventConfig>>) ckass);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new JustThrowException(e);
            }
            
        }
    }
    
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
    
    @Override
    public Pipeline pipeline()
    {
        return eventBus.pipeline();
    }
}
