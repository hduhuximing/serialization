package com.jfireframework.eventbus.executor;

import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;

public class ParallelHandlerExecutor implements EventExecutor
{
    
    @Override
    public void handle(EventContext<?> eventContext, RunnerMode runnerMode)
    {
        EventHelper.handle(eventContext, runnerMode);
    }
    
}
