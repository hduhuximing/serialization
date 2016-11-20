package com.jfireframework.eventbus.pipeline.conversion;

import com.jfireframework.eventbus.pipeline.Pipeline;

public interface Conversion<E>
{
    public void conversie(E data, Pipeline pipeline);
}
