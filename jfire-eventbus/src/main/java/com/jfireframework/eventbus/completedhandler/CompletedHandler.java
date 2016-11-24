package com.jfireframework.eventbus.completedhandler;

import com.jfireframework.eventbus.util.RunnerMode;

public interface CompletedHandler<E>
{
    public void onCompleted(E result, RunnerMode runnerMode);
    
    public void onError(Throwable e, RunnerMode runnerMode);
}
