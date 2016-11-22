package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.RowEventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class RowEventContextImpl<T> extends NormalEventContext<T> implements RowEventContext<T>
{
    private final Object rowkey;
    
    public RowEventContextImpl(Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, EventExecutor executor, EventBus eventBus, Object rowkey)
    {
        super(eventData, event, handler, executor, eventBus);
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
