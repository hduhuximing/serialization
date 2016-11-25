package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public abstract class BasePipeline implements Pipeline
{
    protected Pipeline       next;
    protected final Pipeline pred;
    protected final Operator operator;
    
    public BasePipeline(Operator operator, Pipeline pred)
    {
        this.operator = operator;
        this.pred = pred;
    }
    
    @Override
    public Pipeline add(final Operator operator)
    {
        next = new DefaultPipeline(operator, this);
        return next;
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        if (next != null)
        {
            next.work(result, runnerMode);
        }
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        if (next != null)
        {
            next.onError(e, runnerMode);
        }
    }
    
    @Override
    public void work(Object upstreamResult, RunnerMode runnerMode)
    {
        operator.work(upstreamResult, this, runnerMode);
    }
    
    @Override
    public void start()
    {
        pred.start();
    }
    
    @Override
    public void start(Object initParam)
    {
        pred.start(initParam);
    }
    
}
