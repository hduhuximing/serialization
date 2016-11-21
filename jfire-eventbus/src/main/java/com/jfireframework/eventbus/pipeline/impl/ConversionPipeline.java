package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.pipeline.conversion.Conversion;

public class ConversionPipeline extends AbstractPipeline
{
    private final Conversion<Object> conversion;
    
    @SuppressWarnings("unchecked")
    public ConversionPipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap, Pipeline pre, final Conversion<?> conversion)
    {
        super(eventBus, executorMap, pre, null, null, null);
        this.conversion = (Conversion<Object>) conversion;
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        if (conversion.conversie(upstreamResult, this))
        {
            this.result = upstreamResult;
            signal();
        }
    }
    
    @Override
    public void onCompleted(Object result)
    {
        if (pipelineCompletedHandler != null)
        {
            pipelineCompletedHandler.onCompleted(result);
        }
    }
}
