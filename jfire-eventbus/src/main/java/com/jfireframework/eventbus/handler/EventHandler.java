package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.util.RunnerMode;

public interface EventHandler<E>
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public Object handle(E data, RunnerMode runnerMode);
    
}
