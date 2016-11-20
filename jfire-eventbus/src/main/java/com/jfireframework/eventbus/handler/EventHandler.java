package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.bus.EventBus;

public interface EventHandler<E>
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public Object handle(E data, EventBus eventBus);
    
}
