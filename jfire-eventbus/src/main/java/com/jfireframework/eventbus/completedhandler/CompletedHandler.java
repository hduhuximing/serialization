package com.jfireframework.eventbus.completedhandler;

public interface CompletedHandler<E>
{
    public void onCompleted(E result);
    
    public void onError(Throwable e);
}
