package com.jfireframework.coordinator.bus.impl;

import java.util.concurrent.Executors;

public class ComputationCoordinatorBus extends AbstractCoordinatorBus
{
    
    public ComputationCoordinatorBus()
    {
        super(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }
    
    public ComputationCoordinatorBus(int coreThreadNum)
    {
        super(Executors.newFixedThreadPool(coreThreadNum));
    }
    
}
