package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.handler.EventHandler;

public class PipelineData
{
    private final Object                      eventData;
    private final Object                      rowKey;
    private final Enum<? extends EventConfig> event;
    private final EventHandler<?>             handler;
    
    public PipelineData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowKey)
    {
        this.eventData = eventData;
        this.rowKey = rowKey;
        this.event = event;
        this.handler = handler;
    }
    
    public PipelineData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData)
    {
        this(event, handler, eventData, USE_UPSTREAM_RESULT);
    }
    
    public PipelineData(Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        this(event, handler, USE_UPSTREAM_RESULT, USE_UPSTREAM_RESULT);
    }
    
    public Object getEventData()
    {
        return eventData;
    }
    
    public Object getRowKey()
    {
        return rowKey;
    }
    
    public Enum<? extends EventConfig> getEvent()
    {
        return event;
    }
    
    public EventHandler<?> getHandler()
    {
        return handler;
    }
    
}
