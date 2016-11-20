package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.completedhandler.impl.CallNextPipeline;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.eventcontext.impl.NormalEventContext;
import com.jfireframework.eventbus.eventcontext.impl.ReadWriteEventContextImpl;
import com.jfireframework.eventbus.eventcontext.impl.RowEventContextImpl;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public abstract class AbstractPipeline implements Pipeline
{
    protected final IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap;
    protected final EventBus                                                    eventBus;
    protected final Pipeline                                                    pre;
    protected Thread                                                            owner;
    protected volatile boolean                                                  finished            = false;
    protected volatile boolean                                                  await               = false;
    protected Throwable                                                         e;
    protected CompletedHandler<Object>                                          completedHandler;
    protected final Object                                                      eventData;
    protected final Enum<? extends EventConfig>                                 event;
    protected final EventHandler<?>                                             handler;
    protected final Object                                                      rowKey;
    public static final Object                                                  USE_UPSTREAM_RESULT = new Object();
    protected Object                                                            result;
    
    public AbstractPipeline(EventBus eventBus, Pipeline pre, Enum<? extends EventConfig> event, EventHandler<?> handler, Object rowKey, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap)
    {
        eventData = USE_UPSTREAM_RESULT;
        this.executorMap = executorMap;
        this.eventBus = eventBus;
        this.pre = pre;
        this.event = event;
        this.handler = handler;
        this.rowKey = rowKey;
    }
    
    public AbstractPipeline(EventBus eventBus, Pipeline pre, Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, Object rowKey, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap)
    {
        this.executorMap = executorMap;
        this.eventBus = eventBus;
        this.pre = pre;
        this.eventData = eventData;
        this.event = event;
        this.handler = handler;
        this.rowKey = rowKey;
    }
    
    @Override
    public Pipeline add(Object eventData, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        Pipeline pipeline = new WorkPipeline(eventBus, this, eventData, event, handler, rowkey, executorMap);
        completedHandler = new CallNextPipeline(pipeline);
        return pipeline;
    }
    
    @Override
    public Pipeline add(Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        Pipeline pipeline = new WorkPipeline(eventBus, this, eventData, event, handler, null, executorMap);
        completedHandler = new CallNextPipeline(pipeline);
        return pipeline;
    }
    
    @Override
    public Pipeline add(Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        Pipeline pipeline = new WorkPipeline(eventBus, this, event, handler, rowkey, executorMap);
        completedHandler = new CallNextPipeline(pipeline);
        return pipeline;
    }
    
    @Override
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        Pipeline pipeline = new WorkPipeline(eventBus, this, event, handler, null, executorMap);
        completedHandler = new CallNextPipeline(pipeline);
        return pipeline;
    }
    
    @Override
    public void onCompleted(Object result)
    {
        this.result = result;
        if (completedHandler != null)
        {
            try
            {
                completedHandler.onCompleted(result);
            }
            finally
            {
                ;
            }
        }
        signal();
    }
    
    @Override
    public void start()
    {
        if (pre != null)
        {
            pre.start();
        }
        else
        {
            work(null);
        }
        
    }
    
    @Override
    public void await()
    {
        owner = Thread.currentThread();
        await = true;
        while (finished == false)
        {
            LockSupport.park();
        }
    }
    
    protected void signal()
    {
        finished = true;
        if (await)
        {
            LockSupport.unpark(owner);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void completedHanlder(CompletedHandler<?> completedHandler)
    {
        this.completedHandler = (CompletedHandler<Object>) completedHandler;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected EventContext<?> build(Object eventData)
    {
        if (rowKey == null)
        {
            EventConfig config = (EventConfig) event;
            if (config.parallelLevel() == ParallelLevel.RW_EVENT_READ)
            {
                return new ReadWriteEventContextProxy(ReadWriteEventContext.READ, eventData, event, handler, executorMap.get(event), eventBus);
            }
            else if (config.parallelLevel() == ParallelLevel.RW_EVENT_WRITE)
            {
                return new ReadWriteEventContextProxy(ReadWriteEventContext.WRITE, eventData, event, handler, executorMap.get(event), eventBus);
            }
            else
            {
                return new NormalEventContextProxy(eventData, event, handler, executorMap.get(event), eventBus);
            }
        }
        else
        {
            return new RowEventContextProxy(eventData, event, handler, executorMap.get(event), eventBus, rowKey);
        }
    }
    
    public void onError(Throwable e)
    {
        this.e = e;
    }
    
    public Object getResult()
    {
        return result;
    }
    
    public Throwable getThrowable()
    {
        return e;
    }
    
    class ReadWriteEventContextProxy<T> extends ReadWriteEventContextImpl<T>
    {
        
        public ReadWriteEventContextProxy(int mode, Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, EventExecutor executor, EventBus eventBus)
        {
            super(mode, eventData, event, handler, executor, eventBus);
        }
        
        public void signal(Object result)
        {
            super.signal(result);
            onCompleted(result);
        }
        
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            onError(e);
        }
    }
    
    class RowEventContextProxy<T> extends RowEventContextImpl<T>
    {
        
        public RowEventContextProxy(Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, EventExecutor executor, EventBus eventBus, Object rowkey)
        {
            super(eventData, event, handler, executor, eventBus, rowkey);
        }
        
        public void signal(Object result)
        {
            super.signal(result);
            onCompleted(result);
        }
        
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            onError(e);
        }
    }
    
    class NormalEventContextProxy<T> extends NormalEventContext<T>
    {
        
        public NormalEventContextProxy(Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, EventExecutor executor, EventBus eventBus)
        {
            super(eventData, event, handler, executor, eventBus);
        }
        
        public void signal(Object result)
        {
            super.signal(result);
            onCompleted(result);
        }
        
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            onError(e);
        }
    }
}
