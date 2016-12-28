package com.jfireframework.coordinator.handlercontext.impl;

import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.executor.EventExecutor;
import com.jfireframework.coordinator.handlercontext.RowCoordinatorContext;
import com.jfireframework.coordinator.util.RunnerMode;

public class RowCoordinatorContextImpl<T> extends NormalCoordinatorContext<T> implements RowCoordinatorContext<T>
{
    private final Object rowkey;
    
    public RowCoordinatorContextImpl(RunnerMode runnerMode, Object eventData, CoordinatorHandler<?> handler, EventExecutor executor, Object rowkey)
    {
        super(runnerMode, eventData, handler, executor);
        this.rowkey = rowkey;
        if (rowkey == null)
        {
            throw new IllegalArgumentException("rowkey不能为null");
        }
    }
    
    @Override
    public Object rowkey()
    {
        return rowkey;
    }
    
}
