package com.jfireframework.coordinator.bus;

import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.pipeline.Pipeline;

public interface CoordinatorBus
{
    
    public void stop();
    
    public void post(CoordinatorContext<?> eventContext);
    
    public Pipeline pipeline();
    
    public <T> CoordinatorContext<T> post(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data, Object rowkey);
    
    public <T> CoordinatorContext<T> post(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data);
    
}
