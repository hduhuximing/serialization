package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.completedhandler.impl.CallNextPipeline;
import com.jfireframework.eventbus.pipeline.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public abstract class AbstractPipeline implements Pipeline
{
    protected CompletedHandler<Object> pipelineCompletedHandler;
    
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
            public void onCompleted(Object result, RunnerMode runnerMode)
            {
                operator.onCompleted(result, runnerMode);
                if (pipelineCompletedHandler != null)
                {
                    pipelineCompletedHandler.onCompleted(result, runnerMode);
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                operator.onError(e, runnerMode);
                if (pipelineCompletedHandler != null)
                {
                    pipelineCompletedHandler.onError(e, runnerMode);
                }
            }
            
        };
        pipelineCompletedHandler = new CallNextPipeline(pipeline);
        return pipeline;
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        if (pipelineCompletedHandler != null)
        {
            pipelineCompletedHandler.onCompleted(result, runnerMode);
        }
    }
    
    @Override
    public void setCompletedHanlder(CompletedHandler<Object> completedHandler)
    {
        this.pipelineCompletedHandler = completedHandler;
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        if (pipelineCompletedHandler != null)
        {
            pipelineCompletedHandler.onError(e, runnerMode);
        }
    }
    
}
