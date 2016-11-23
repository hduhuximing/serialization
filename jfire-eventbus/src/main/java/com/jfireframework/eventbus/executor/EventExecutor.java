package com.jfireframework.eventbus.executor;

import com.jfireframework.eventbus.eventcontext.EventContext;

public interface EventExecutor
{
    public void handle(EventContext<?> eventContext);
}
