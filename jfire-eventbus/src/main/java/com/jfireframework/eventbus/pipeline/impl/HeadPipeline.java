package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.executor.EventExecutor;

public class HeadPipeline extends AbstractPipeline
{
    
    public HeadPipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap)
    {
        super(eventBus, executorMap, null, null, null, null);
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        onCompleted(upstreamResult);
    }
    
}
