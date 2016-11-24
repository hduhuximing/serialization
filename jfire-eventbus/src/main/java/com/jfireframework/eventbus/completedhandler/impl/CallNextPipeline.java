package com.jfireframework.eventbus.completedhandler.impl;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public class CallNextPipeline implements CompletedHandler<Object>
{
    private final Pipeline pipeline;
    
    public CallNextPipeline(Pipeline pipeline)
    {
        this.pipeline = pipeline;
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        pipeline.work(result, runnerMode);
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        pipeline.onError(e, runnerMode);
    }
    
}
