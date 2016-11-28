package com.jfireframework.eventbus.operator.impl;

import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public class ResultOp implements Operator
{
    private Object    result;
    private Throwable e;
    
    @Override
    public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
    {
        result = data;
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        this.e = e;
    }
    
    public Object getResult()
    {
        return result;
    }
    
    public void setResult(Object result)
    {
        this.result = result;
    }
    
    public boolean hasError()
    {
        return e != null;
    }
    
    public Throwable getE()
    {
        return e;
    }
    
    public void setE(Throwable e)
    {
        this.e = e;
    }
    
}
