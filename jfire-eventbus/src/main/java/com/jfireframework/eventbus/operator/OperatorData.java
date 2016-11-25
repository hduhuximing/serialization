package com.jfireframework.eventbus.operator;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class OperatorData
{
    private final Object                      eventData;
    private final Object                      rowKey;
    private final Enum<? extends EventConfig> event;
    private final EventHandler<?>             handler;
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowKey)
    {
        this.eventData = eventData;
        this.rowKey = rowKey;
        this.event = event;
        this.handler = handler;
    }
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData)
    {
        this(event, handler, eventData, Pipeline.USE_UPSTREAM_RESULT);
    }
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        this(event, handler, Pipeline.USE_UPSTREAM_RESULT, Pipeline.USE_UPSTREAM_RESULT);
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
