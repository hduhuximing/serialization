package com.jfireframework.eventbus.completedhandler.impl;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class CallNextPipeline implements CompletedHandler<Object>
{
    private final Pipeline pipeline;
    
    public CallNextPipeline(Pipeline pipeline)
    {
        this.pipeline = pipeline;
    }
    
    @Override
    public void onCompleted(Object result)
    {
        pipeline.work(result);
    }
    
}
