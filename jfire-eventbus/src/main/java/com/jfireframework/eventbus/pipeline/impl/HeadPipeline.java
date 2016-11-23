package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.bus.EventBus;

public class HeadPipeline extends AbstractPipeline
{
    
    public HeadPipeline(EventBus eventBus)
    {
        super(eventBus, null, null, null, null, null);
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        onCompleted(upstreamResult);
    }
    
    @Override
    public void start()
    {
        work(null);
    }
    
    @Override
    public void start(Object initParam)
    {
        work(initParam);
    }
    
}
