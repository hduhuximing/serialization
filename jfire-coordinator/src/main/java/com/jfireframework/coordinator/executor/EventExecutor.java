package com.jfireframework.coordinator.executor;

import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.util.RunnerMode;

public interface EventExecutor
{
    public void handle(CoordinatorContext<?> eventContext, RunnerMode runnerMode);
}
