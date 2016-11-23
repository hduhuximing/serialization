package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.Executors;
import com.jfireframework.eventbus.util.DefaultWorkerCount;
import com.jfireframework.eventbus.util.WorkerCount;

public class IoEventBus extends AbstractEventBus
{
    
    public IoEventBus(int maxEventWorkerNum, long waitTime)
    {
        this(Runtime.getRuntime().availableProcessors(), maxEventWorkerNum, waitTime);
    }
    
    public IoEventBus(int coreEventThreadNum, int maxEventWorkerNum, long waitTime)
    {
        this(new DefaultWorkerCount(), waitTime, coreEventThreadNum, maxEventWorkerNum);
    }
    
    public IoEventBus(WorkerCount workerCount, long waitTime, int coreEventWorkerNum, int maxEventWorkerNum)
    {
        pool = Executors.newCachedThreadPool();
    }
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
    @Override
    public void addWorker()
    {
        ;
    }
    
    @Override
    public void reduceWorker()
    {
        throw new UnsupportedOperationException();
    }
    
}
