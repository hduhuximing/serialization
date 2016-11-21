package com.jfireframework.eventbus.pipeline.conversion;

import com.jfireframework.eventbus.pipeline.Pipeline;

public class FromIterable<E> implements Conversion<Iterable<E>>
{
    @Override
    public boolean conversie(Iterable<E> data, Pipeline pipeline)
    {
        for (Object each : data)
        {
            pipeline.onCompleted(each);
        }
        return true;
    }
    
}
