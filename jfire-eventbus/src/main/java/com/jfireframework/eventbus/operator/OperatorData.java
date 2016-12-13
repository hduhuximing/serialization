package com.jfireframework.eventbus.operator;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.pipeline.InternalPipeline;
import com.jfireframework.eventbus.pipeline.RowKeyFetcher;

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
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, RowKeyFetcher<?> rowKey)
    {
        this.eventData = eventData;
        this.rowKey = rowKey;
        this.event = event;
        this.handler = handler;
    }
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData)
    {
        this(event, handler, eventData, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        this(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    public OperatorData(Enum<? extends EventConfig> event, EventHandler<?> handler, RowKeyFetcher<?> rowKeyFetcher)
    {
        this(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, rowKeyFetcher);
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
