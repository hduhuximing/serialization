package com.jfireframework.eventbus.pipeline.conversion;

import com.jfireframework.eventbus.pipeline.Pipeline;

public abstract class MapConversion<E> implements Conversion<E>
{
    
    @Override
    public boolean conversie(E data, Pipeline pipeline)
    {
        Object mapped = conversie(data);
        pipeline.onCompleted(mapped);
        return true;
    }
    
    protected abstract Object conversie(E data);
    
}
