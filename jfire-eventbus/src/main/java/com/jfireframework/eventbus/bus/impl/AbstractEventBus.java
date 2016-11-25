package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.ExecutorService;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.pipeline.impl.DefaultPipeline;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;
import com.jfireframework.eventbus.util.RunnerMode.ThreadMode;

public abstract class AbstractEventBus implements EventBus
{
    protected final MPMCQueue<EventContext<?>> eventQueue = new MPMCQueue<EventContext<?>>();
    protected static final Logger              LOGGER     = ConsoleLogFactory.getLogger();
    protected ExecutorService                  pool;
    
    public AbstractEventBus(ExecutorService pool)
    {
        this.pool = pool;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentEventbus, this);
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(runnerMode, event, handler, data);
        post(eventContext);
        return eventContext;
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentEventbus, this);
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(runnerMode, event, handler, data, rowkey);
        post(eventContext);
        return eventContext;
    }
    
    @Override
    public void post(EventContext<?> eventContext)
    {
        pool.submit(eventContext);
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentEventbus, this);
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(runnerMode, event, handler, data, rowkey);
        eventContext.run();
        eventContext.await();
        return eventContext;
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentThread, null);
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(runnerMode, event, handler, data);
        eventContext.run();
        eventContext.await();
        return eventContext;
    }
    
    @Override
    public Pipeline pipeline()
    {
        return DefaultPipeline.create().switchMode(this);
    }
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
}
