package com.jfireframework.coordinator.util;

import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.api.ParallelLevel;

public enum DefaultCoordinator implements CoordinatorConfig
{
    SWITCH, JUST_PAEALLEL_EVENT;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
}
