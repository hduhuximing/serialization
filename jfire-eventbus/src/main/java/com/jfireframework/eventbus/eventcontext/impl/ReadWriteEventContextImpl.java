package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class ReadWriteEventContextImpl<T> extends NormalEventContext<T> implements ReadWriteEventContext<T>
{
    private boolean   immediateMode = false;
    private final int mode;
    
    public ReadWriteEventContextImpl(int mode, Object eventData, EventHandler<?> handler, EventExecutor executor)
    {
        super(eventData, handler, executor);
        this.mode = mode;
    }
    
    @Override
    public boolean immediateInvoke()
    {
        return immediateMode;
    }
    
    @Override
    public int mode()
    {
        return mode;
    }
    
    @Override
    public void setImmediateMode()
    {
        immediateMode = true;
    }
    
}
