package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.conversion.Conversion;

public class Pipelines
{
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowkey)
    {
        return null;
        
    }
    
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData)
    {
        return null;
        
    }
    
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        return null;
        
    }
    
    public Pipeline addAll(PipelineData... events)
    {
        return null;
        
    }
    
    public Pipeline conversion(Conversion<?> conversion)
    {
        return null;
        
    }
}
