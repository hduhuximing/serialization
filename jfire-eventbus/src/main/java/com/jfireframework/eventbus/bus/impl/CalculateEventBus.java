package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.Executors;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.CalculateWorker;

public class CalculateEventBus extends AbstractEventBus
{
    private final MPMCQueue<EventWorker> workers = new MPMCQueue<EventWorker>();
    
    public CalculateEventBus()
    {
        this(Runtime.getRuntime().availableProcessors() * 2 + 1);
    }
    
    public CalculateEventBus(int coreThreadNum)
    {
        pool = Executors.newFixedThreadPool(coreThreadNum);
    }
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
    @Override
    public void addWorker()
    {
        EventWorker worker = new CalculateWorker(this, eventQueue);
        pool.submit(worker);
        workers.offer(worker);
    }
    
    @Override
    public void reduceWorker()
    {
        EventWorker worker = workers.poll();
        if (worker != null)
        {
            worker.stop();
        }
    }
    
}
