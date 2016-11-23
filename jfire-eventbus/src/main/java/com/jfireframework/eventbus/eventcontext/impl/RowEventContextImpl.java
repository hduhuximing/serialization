package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.eventcontext.RowEventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class RowEventContextImpl<T> extends NormalEventContext<T> implements RowEventContext<T>
{
    private final Object rowkey;
    
    public RowEventContextImpl(Object eventData, EventHandler<?> handler, EventExecutor executor, Object rowkey)
    {
        super(eventData, handler, executor);
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
