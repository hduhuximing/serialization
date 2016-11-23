package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public interface EventBus
{
    public void register(Class<? extends Enum<? extends EventConfig>>... ckasses);
    
    public void register(Class<? extends Enum<? extends EventConfig>> ckass);
    
    /**
     * 找出该事件合适的执行单元
     * 
     * @param event
     * @return
     */
    public EventExecutor findExecutor(Enum<? extends EventConfig> event);
    
    public void addWorker();
    
    public void reduceWorker();
    
    public void stop();
    
    public void post(EventContext<?> eventContext);
    
    public Pipeline pipeline();
    
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler);
}
