package com.jfireframework.coordinator.operator;

import com.jfireframework.coordinator.util.RunnerMode;

public abstract class OperatorAdapter implements Operator
{
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        ;
    }
    
    @Override
    public void onComplete(Object result, RunnerMode runnerMode)
    {
        ;
    }
    
}
