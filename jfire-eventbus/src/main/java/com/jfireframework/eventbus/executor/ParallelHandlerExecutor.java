package com.jfireframework.eventbus.executor;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.util.EventHelper;

public class ParallelHandlerExecutor implements EventExecutor
{
    
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        EventHelper.handle(eventContext, eventBus);
    }
    
}
