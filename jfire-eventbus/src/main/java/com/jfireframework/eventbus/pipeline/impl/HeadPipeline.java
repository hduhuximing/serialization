package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.util.RunnerMode;

public class HeadPipeline extends AbstractPipeline
{
    
    @Override
    public void work(Object upstreamResult, RunnerMode runnerMode)
    {
        if (pipelineCompletedHandler != null)
        {
            pipelineCompletedHandler.onCompleted(upstreamResult, runnerMode);
        }
    }
    
    @Override
    public void start()
    {
        work(null, null);
    }
    
    @Override
    public void start(Object initParam)
    {
        work(initParam, null);
    }
    
}
