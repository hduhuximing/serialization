package com.jfireframework.eventbus.operator.impl;

import com.jfireframework.baseutil.concurrent.SingleSync;
import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.pipeline.InternalPipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public class SingleAwaitOp implements Operator
{
    private SingleSync sync = new SingleSync();
    private Throwable  e;
    private Object     result;
    
    @Override
    public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
    {
        pipeline.onNext(data, runnerMode);
    }
    
    public void await()
    {
        sync.await();
    }
    
    public Throwable getE()
    {
        sync.await();
        return e;
    }
    
    public Object getResult()
    {
        sync.await();
        return result;
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        this.e = e;
        sync.signal();
    }
    
    @Override
    public void onComplete(Object result, RunnerMode runnerMode)
    {
        this.result = result;
        sync.signal();
    }
}
