package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;

public interface Conversion<E>
{
    public void conversie(E data, CompletedHandler<Object> handler, Pipeline pipeline);
}
