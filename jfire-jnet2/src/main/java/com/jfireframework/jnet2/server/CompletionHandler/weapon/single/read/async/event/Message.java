package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.api.ParallelLevel;

public enum Message implements CoordinatorConfig
{
    recive;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
    
}
