package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.util.RunnerMode;

public interface Operator
{
    public void work(Object data, Pipeline pipeline, RunnerMode runnerMode);
    
    public void onCompleted(Object result, RunnerMode runnerMode);
    
    public void onError(Throwable e, RunnerMode runnerMode);
}
