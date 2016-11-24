package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.Executors;

public class IoEventBus extends AbstractEventBus
{
    
    public IoEventBus()
    {
        super(Executors.newCachedThreadPool());
    }
    
}
