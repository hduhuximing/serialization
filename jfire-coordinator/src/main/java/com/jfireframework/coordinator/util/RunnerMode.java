package com.jfireframework.coordinator.util;

import com.jfireframework.coordinator.bus.CoordinatorBus;

public class RunnerMode
{
    private final ThreadMode threadMode;
    private final CoordinatorBus   eventBus;
    
    public static enum ThreadMode
    {
        currentThread, newThread, io, computation, currentEventbus
    }
    
    public RunnerMode(ThreadMode threadMode, CoordinatorBus eventBus)
    {
        this.threadMode = threadMode;
        this.eventBus = eventBus;
    }
    
    public ThreadMode getThreadMode()
    {
        return threadMode;
    }
    
    public CoordinatorBus getEventBus()
    {
        return eventBus;
    }
    
}
