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
    
    public WorkPipeline(EventBus eventBus, Pipeline pre, Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, Object rowKey, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap)
    {
        super(eventBus, pre, eventData, event, handler, rowKey, executorMap);
    }
    
    public WorkPipeline(EventBus eventBus, Pipeline pre, Enum<? extends EventConfig> event, EventHandler<?> handler, Object rowKey, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap)
    {
        super(eventBus, pre, event, handler, rowKey, executorMap);
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
