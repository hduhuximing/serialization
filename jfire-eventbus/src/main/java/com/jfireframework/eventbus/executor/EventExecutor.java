package com.jfireframework.eventbus.executor;

import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.util.RunnerMode;

public interface EventExecutor
{
    public void handle(EventContext<?> eventContext, RunnerMode runnerMode);
}
