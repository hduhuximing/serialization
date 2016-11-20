package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.executor.EventExecutor;

public class HeadPipeline extends AbstractPipeline
{
    
    public HeadPipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap)
    {
        super(eventBus, null, null, null, null, executorMap);
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        onCompleted(upstreamResult);
    }
    
}
