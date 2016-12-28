package com.jfireframework.coordinator.api;

import com.jfireframework.coordinator.util.RunnerMode;

public interface CoordinatorHandler<E>
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public Object handle(E data, RunnerMode runnerMode);
    
}
