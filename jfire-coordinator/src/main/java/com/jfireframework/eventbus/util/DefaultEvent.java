package com.jfireframework.eventbus.util;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum DefaultEvent implements EventConfig
{
    SWITCH, JUST_PAEALLEL_EVENT;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
}
