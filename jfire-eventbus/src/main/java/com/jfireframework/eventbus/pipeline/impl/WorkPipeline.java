package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class WorkPipeline extends AbstractPipeline
{
    
    public WorkPipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap, Pipeline pre, Enum<? extends EventConfig> event, EventHandler<?> handler, Object rowKey)
    {
        super(eventBus, executorMap, pre, event, handler, rowKey);
    }
    
    public WorkPipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap, Pipeline pre, Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, Object rowKey)
    {
        super(eventBus, executorMap, pre, eventData, event, handler, rowKey);
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        EventContext<?> eventContext;
        if (eventData == USE_UPSTREAM_RESULT)
        {
            eventContext = build(upstreamResult);
        }
        else
        {
            eventContext = build(eventData);
        }
        eventBus.post(eventContext);
    }
    
}
