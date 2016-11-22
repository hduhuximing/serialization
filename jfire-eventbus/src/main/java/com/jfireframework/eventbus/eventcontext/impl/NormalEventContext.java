package com.jfireframework.eventbus.eventcontext.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class NormalEventContext<T> implements EventContext<T>
{
    protected final EventBus                    eventBus;
    protected final EventExecutor               executor;
    protected final EventHandler<?>             handler;
    protected final Object                      eventData;
    protected final Enum<? extends EventConfig> event;
    protected volatile boolean                  finished = false;
    protected Thread                            owner;
    protected volatile boolean                  await    = false;
    protected Throwable                         e;
    protected T                                 result;
    
    public NormalEventContext(Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler, EventExecutor executor, EventBus eventBus)
    {
        this.eventData = eventData;
        this.event = event;
        this.handler = handler;
        this.executor = executor;
        this.eventBus = eventBus;
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
    
    @Override
    public void setThrowable(Throwable e)
    {
        this.e = e;
        signal();
    }
    
    @Override
    public Throwable getThrowable()
    {
        return e;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void setResult(Object result)
    {
        this.result = (T) result;
        signal();
    }
    
    protected void signal()
    {
        finished = true;
        if (await)
        {
            LockSupport.unpark(owner);
        }
    }
    
    @Override
    public void await(long mills)
    {
        owner = Thread.currentThread();
        await = true;
        long left = TimeUnit.MILLISECONDS.toNanos(mills);
        while (finished == false)
        {
            long t0 = System.nanoTime();
            LockSupport.parkNanos(left);
            long t1 = System.nanoTime();
            left -= t1 - t0;
            if (left < 1000)
            {
                // 1000纳秒其实非常短，使用循环等待就好了
                for (int i = 0; i < 10000; i++)
                {
                    ;
                }
                break;
            }
        }
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public Object getEventData()
    {
        return eventData;
    }
    
    @Override
    public Enum<? extends EventConfig> getEvent()
    {
        return event;
    }
    
    @Override
    public T getResult()
    {
        if (finished)
        {
            return result;
        }
        await();
        return result;
    }
    
    @Override
    public EventExecutor executor()
    {
        return executor;
    }
    
    @Override
    public T getResult(long mills) throws InterruptedException
    {
        if (finished)
        {
            return result;
        }
        await(mills);
        if (finished)
        {
            return result;
        }
        else
        {
            throw new InterruptedException();
        }
    }
    
    @Override
    public EventHandler<?> eventHandler()
    {
        return handler;
    }
    
}
