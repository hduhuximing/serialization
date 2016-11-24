package com.jfireframework.eventbus.util.extra;

import com.jfireframework.baseutil.concurrent.SingleSync;
import com.jfireframework.eventbus.pipeline.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public class SingleAwaitOp implements Operator
{
    private SingleSync sync = new SingleSync();
    private Throwable  e;
    private Object     result;
    
    @Override
    public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
    {
        result = data;
        pipeline.onCompleted(result, runnerMode);
    }
    
    public void await()
    {
        sync.await();
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        sync.signal();
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        this.e = e;
        sync.signal();
    }
    
    public Throwable getE()
    {
        return e;
    }
    
    public Object getResult()
    {
        return result;
    }
}
