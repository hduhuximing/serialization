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
        result = data;
        sync.signal();
        pipeline.onCompleted(result, runnerMode);
    }
    
    public void await()
    {
        sync.await();
    }
    
    public Throwable getE()
    {
        return e;
    }
    
    public Object getResult()
    {
        return result;
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        this.e = e;
        sync.signal();
    }
}
