package com.jfireframework.coordinator.bus;

import com.jfireframework.coordinator.bus.impl.ComputationCoordinatorBus;
import com.jfireframework.coordinator.bus.impl.IoCoordinatorBus;

public class CoordinatorBuses
{
    private static final CoordinatorBus computationEventBus = new ComputationCoordinatorBus();
    private static final CoordinatorBus ioEventBus          = new IoCoordinatorBus();
    
    public static CoordinatorBus computation()
    {
        return computationEventBus;
    }
    
    public static CoordinatorBus io()
    {
        return ioEventBus;
    }
}
