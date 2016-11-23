package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.bus.impl.CalculateEventBus;

public class EventBuses
{
    private static final EventBus computationEventBus = new CalculateEventBus();
    
    public static EventBus computation()
    {
        return computationEventBus;
    }
}
