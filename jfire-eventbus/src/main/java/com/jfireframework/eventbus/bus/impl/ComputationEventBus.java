package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.Executors;

public class ComputationEventBus extends AbstractEventBus
{
    
    public ComputationEventBus()
    {
        super(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }
    
    public ComputationEventBus(int coreThreadNum)
    {
        super(Executors.newFixedThreadPool(coreThreadNum));
    }
    
}
