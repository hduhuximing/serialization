package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.pipeline.InternalPipeline;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public abstract class BasePipeline implements InternalPipeline, Pipeline
{
    protected InternalPipeline       next;
    protected final InternalPipeline pred;
    protected final Operator         operator;
    
    public BasePipeline(Operator operator, InternalPipeline pred)
    {
        this.operator = operator;
        this.pred = pred;
    }
    
    @Override
    public InternalPipeline internalAdd(final Operator operator)
    {
        next = new DefaultPipeline(operator, this);
        return next;
    }
    
    /**
     * 本节点逻辑任务完成后会调用该方法,将上游结果传递到下方
     * 
     * @param result
     */
    @Override
    public void onNext(Object result, RunnerMode runnerMode)
    {
        try
        {
            if (next != null)
            {
                next.work(result, runnerMode);
            }
            else
            {
                onCompleted(result, runnerMode);
            }
        }
        catch (Throwable e)
        {
            onError(e, runnerMode);
        }
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        operator.onComplete(result, runnerMode);
        if (next != null)
        {
            next.onCompleted(result, runnerMode);
        }
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        operator.onError(e, runnerMode);
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
    public void internalStart()
    {
        pred.internalStart();
    }
    
    @Override
    public void internalStart(Object initParam)
    {
        pred.internalStart(initParam);
    }
    
    @Override
    public void start()
    {
        internalStart();
    }
    
    @Override
    public void start(Object initParam)
    {
        internalStart(initParam);
    }
    
    @Override
    public Pipeline add(Operator operator)
    {
        return (Pipeline) internalAdd(operator);
    }
}
