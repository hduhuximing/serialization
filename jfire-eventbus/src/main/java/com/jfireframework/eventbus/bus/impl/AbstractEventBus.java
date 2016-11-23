package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.ExecutorService;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.pipeline.impl.HeadPipeline;
import com.jfireframework.eventbus.util.EventHelper;

public abstract class AbstractEventBus implements EventBus
{
    protected final MPMCQueue<EventContext<?>> eventQueue = new MPMCQueue<EventContext<?>>();
    protected static final Logger              LOGGER     = ConsoleLogFactory.getLogger();
    protected ExecutorService                  pool;
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(data, event, handler);
        post(eventContext);
        return eventContext;
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(data, event, rowkey, handler);
        post(eventContext);
        return eventContext;
    }
    
    @Override
    public void post(EventContext<?> eventContext)
    {
        // eventQueue.offerAndSignal(eventContext);
        pool.submit(eventContext);
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(data, event, rowkey, handler);
        // eventContext.executor().handle(eventContext, this);
        eventContext.run();
        eventContext.await();
        return eventContext;
    }
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        EventContext<T> eventContext = (EventContext<T>) EventHelper.build(data, event, handler);
        eventContext.run();
        eventContext.await();
        return eventContext;
    }
    
    @Override
    public Pipeline pipeline()
    {
        return new HeadPipeline(this);
    }
    
}
