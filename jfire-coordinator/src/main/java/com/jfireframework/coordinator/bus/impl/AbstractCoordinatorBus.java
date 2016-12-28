package com.jfireframework.coordinator.bus.impl;

import java.util.concurrent.ExecutorService;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.bus.CoordinatorBus;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.pipeline.Pipeline;
import com.jfireframework.coordinator.pipeline.impl.DefaultPipeline;
import com.jfireframework.coordinator.util.CoordinatorHelper;
import com.jfireframework.coordinator.util.RunnerMode;
import com.jfireframework.coordinator.util.RunnerMode.ThreadMode;

public abstract class AbstractCoordinatorBus implements CoordinatorBus
{
    protected static final Logger              LOGGER     = ConsoleLogFactory.getLogger();
    protected ExecutorService                  pool;
    
    public AbstractCoordinatorBus(ExecutorService pool)
    {
        this.pool = pool;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> CoordinatorContext<T> post(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentEventbus, this);
        CoordinatorContext<T> eventContext = (CoordinatorContext<T>) CoordinatorHelper.build(runnerMode, event, handler, data);
        post(eventContext);
        return eventContext;
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> CoordinatorContext<T> post(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data, Object rowkey)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentEventbus, this);
        CoordinatorContext<T> eventContext = (CoordinatorContext<T>) CoordinatorHelper.build(runnerMode, event, handler, data, rowkey);
        post(eventContext);
        return eventContext;
    }
    
    @Override
    public void post(CoordinatorContext<?> eventContext)
    {
        pool.submit(eventContext);
    }
    
    @Override
    public Pipeline pipeline()
    {
        return DefaultPipeline.create().switchTo(this);
    }
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
}
