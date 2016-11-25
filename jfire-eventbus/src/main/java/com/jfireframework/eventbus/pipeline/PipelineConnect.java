package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.operator.MapOp;
import com.jfireframework.eventbus.operator.OperatorData;

/**
 * 用户提供pipeline的连接操作
 * 
 * @author linbin
 *
 */
public interface PipelineConnect
{
    public Pipeline work(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowKey);
    
    public Pipeline work(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData);
    
    public Pipeline work(Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public Pipeline work(EventHandler<?> handler);
    
    public Pipeline from();
    
    public <E> Pipeline map(final MapOp<E> mapOp);
    
    public Pipeline switchMode(final EventBus eventBus);
    
    public Pipeline distribute(final OperatorData... datas);
}
