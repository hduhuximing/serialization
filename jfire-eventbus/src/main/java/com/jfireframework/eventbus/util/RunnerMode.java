package com.jfireframework.eventbus.util;

import com.jfireframework.eventbus.bus.EventBus;

public class RunnerMode
{
    private final ThreadMode threadMode;
    private final EventBus   eventBus;
    
    public static enum ThreadMode
    {
        currentThread, newThread, io, computation, currentEventbus
    }
    
    public RunnerMode(ThreadMode threadMode, EventBus eventBus)
    {
        this.threadMode = threadMode;
        this.eventBus = eventBus;
    }
    
    public ThreadMode getThreadMode()
    {
        return threadMode;
    }
    
    public EventBus getEventBus()
    {
        return eventBus;
    }
    
}
