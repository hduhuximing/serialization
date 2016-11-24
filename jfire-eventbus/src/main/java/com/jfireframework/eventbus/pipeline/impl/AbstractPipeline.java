package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.pipeline.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public abstract class AbstractPipeline implements Pipeline
{
    protected Pipeline nextPipeline;
    
    @Override
    public Pipeline add(final Operator operator)
    {
        Pipeline pipeline = new AbstractPipeline() {
            
            @Override
            public void work(Object upstreamResult, RunnerMode runnerMode)
            {
                operator.work(upstreamResult, this, runnerMode);
            }
            
            @Override
            public void start(Object initParam)
            {
                AbstractPipeline.this.start(initParam);
            }
            
            @Override
            public void start()
            {
                AbstractPipeline.this.start();
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                operator.onError(e, runnerMode);
                if (nextPipeline != null)
                {
                    nextPipeline.onError(e, runnerMode);
                }
            }
        };
        nextPipeline = pipeline;
        return pipeline;
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        if (nextPipeline != null)
        {
            nextPipeline.work(result, runnerMode);
        }
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        if (nextPipeline != null)
        {
            nextPipeline.onError(e, runnerMode);
        }
    }
    
}
