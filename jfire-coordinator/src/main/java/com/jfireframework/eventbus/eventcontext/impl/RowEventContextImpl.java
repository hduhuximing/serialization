package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.eventcontext.RowEventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.util.RunnerMode;

public class RowEventContextImpl<T> extends NormalEventContext<T> implements RowEventContext<T>
{
    private final Object rowkey;
    
    public RowEventContextImpl(RunnerMode runnerMode, Object eventData, EventHandler<?> handler, EventExecutor executor, Object rowkey)
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
