package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.bus.impl.ComputationEventBus;
import com.jfireframework.eventbus.bus.impl.IoEventBus;

public class EventBuses
{
    private static final EventBus computationEventBus = new ComputationEventBus();
    private static final EventBus ioEventBus          = new IoEventBus();
    
    public static EventBus computation()
    {
        return computationEventBus;
    }
    
    public static EventBus io()
    {
        return ioEventBus;
    }
}
