package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.pipeline.Conversion;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class ConversionPipeline extends AbstractPipeline
{
    private final Conversion<Object> conversion;
    
    @SuppressWarnings("unchecked")
    public ConversionPipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap, Pipeline pre, Conversion<?> conversion)
    {
        super(eventBus, executorMap, pre, null, null, null);
        this.conversion = (Conversion<Object>) conversion;
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        conversion.conversie(upstreamResult, completedHandler, this);
    }
    
    @Override
    public void onCompleted(Object result)
    {
        this.result = result;
        signal();
    }
}
