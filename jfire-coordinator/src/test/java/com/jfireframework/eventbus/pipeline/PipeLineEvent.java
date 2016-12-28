package com.jfireframework.eventbus.pipeline;

import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.api.ParallelLevel;

public enum PipeLineEvent implements CoordinatorConfig
{
    one, two, three, four;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
    
}
