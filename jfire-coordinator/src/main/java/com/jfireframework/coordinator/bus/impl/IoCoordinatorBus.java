package com.jfireframework.coordinator.bus.impl;

import java.util.concurrent.Executors;

public class IoCoordinatorBus extends AbstractCoordinatorBus
{
    
    public IoCoordinatorBus()
    {
        super(Executors.newCachedThreadPool());
    }
    
}
