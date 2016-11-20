package com.jfireframework.eventbus.completedhandler;

public interface CompletedHandler<E>
{
    public void onCompleted(E result);
}
