package com.jfireframework.coordinator.operator;

import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.pipeline.InternalPipeline;
import com.jfireframework.coordinator.pipeline.RowKeyFetcher;
import com.jfireframework.coordinator.util.DefaultCoordinator;

public class OperatorData
{
    private final Object                      eventData;
    private final Object                      rowKey;
    private final Enum<? extends CoordinatorConfig> event;
    private final CoordinatorHandler<?>             handler;
    
    public OperatorData(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object eventData, Object rowKey)
    {
        this.eventData = eventData;
        this.rowKey = rowKey;
        this.event = event;
        this.handler = handler;
    }
    
    public OperatorData(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object eventData, RowKeyFetcher<?> rowKey)
    {
        this.eventData = eventData;
        this.rowKey = rowKey;
        this.event = event;
        this.handler = handler;
    }
    
    public OperatorData(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object eventData)
    {
        this(event, handler, eventData, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    public OperatorData(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler)
    {
        this(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    public OperatorData(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, RowKeyFetcher<?> rowKeyFetcher)
    {
        this(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, rowKeyFetcher);
    }
    
    public OperatorData(CoordinatorHandler<?> handler)
    {
        this(DefaultCoordinator.JUST_PAEALLEL_EVENT, handler, InternalPipeline.USE_UPSTREAM_RESULT, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    public Object getEventData()
    {
        return eventData;
    }
    
    public Object getRowKey()
    {
        return rowKey;
    }
    
    public Enum<? extends CoordinatorConfig> getEvent()
    {
        return event;
    }
    
    public CoordinatorHandler<?> getHandler()
    {
        return handler;
    }
    
}
