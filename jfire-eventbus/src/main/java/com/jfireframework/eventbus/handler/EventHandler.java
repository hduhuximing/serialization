package com.jfireframework.eventbus.handler;

public interface EventHandler<E>
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public Object handle(E data);
    
}
