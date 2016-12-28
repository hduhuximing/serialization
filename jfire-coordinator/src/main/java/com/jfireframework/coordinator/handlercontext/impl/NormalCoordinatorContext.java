package com.jfireframework.coordinator.handlercontext.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.executor.EventExecutor;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.util.RunnerMode;

public class NormalCoordinatorContext<T> implements CoordinatorContext<T>
{
    protected final RunnerMode      runnerMode;
    protected final EventExecutor   executor;
    protected final CoordinatorHandler<?> handler;
    protected final Object          eventData;
    protected volatile boolean      finished = false;
    protected Thread                owner;
    protected volatile boolean      await    = false;
    protected Throwable             e;
    protected T                     result;
    
    public NormalCoordinatorContext(RunnerMode runnerMode, Object eventData, CoordinatorHandler<?> handler, EventExecutor executor)
    {
        this.runnerMode = runnerMode;
        this.eventData = eventData;
        this.handler = handler;
        this.executor = executor;
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
    public CoordinatorHandler<?> eventHandler()
    {
        return handler;
    }
    
    @Override
    public void run()
    {
        executor.handle(this, runnerMode);
    }
    
    @Override
    public RunnerMode runnerMode()
    {
        return runnerMode;
    }
    
}
