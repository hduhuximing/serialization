package com.jfireframework.coordinator.handlercontext.impl;

import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.executor.EventExecutor;
import com.jfireframework.coordinator.handlercontext.ReadWriteCoordinatorContext;
import com.jfireframework.coordinator.util.RunnerMode;

public class ReadWriteCoordinatorContextImpl<T> extends NormalCoordinatorContext<T> implements ReadWriteCoordinatorContext<T>
{
    private boolean   immediateMode = false;
    private final int mode;
    
    public ReadWriteCoordinatorContextImpl(RunnerMode runnerMode, int mode, Object eventData, CoordinatorHandler<?> handler, EventExecutor executor)
    {
        super(runnerMode, eventData, handler, executor);
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
