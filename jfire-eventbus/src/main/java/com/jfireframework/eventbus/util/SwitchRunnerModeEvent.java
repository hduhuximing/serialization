package com.jfireframework.eventbus.util;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum SwitchRunnerModeEvent implements EventConfig
{
    SWITCH;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
}
