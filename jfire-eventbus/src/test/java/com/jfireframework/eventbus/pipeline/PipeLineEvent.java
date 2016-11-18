package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum PipeLineEvent implements EventConfig
{
    one, two, three, four;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
    
}
