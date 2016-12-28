package com.jfireframework.coordinator.operator.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.coordinator.operator.Operator;
import com.jfireframework.coordinator.pipeline.InternalPipeline;
import com.jfireframework.coordinator.util.RunnerMode;

public class NumberReduceOp implements Operator
{
    private AtomicInteger total = new AtomicInteger();
    private final int     count;
    private Queue<Object> queue = new ConcurrentLinkedQueue<Object>();
    
    public NumberReduceOp(int count)
    {
        this.count = count;
    }
    
    @Override
    public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
    {
        int left = total.incrementAndGet();
        if (left < 0)
        {
            total.set(0);
            left = total.incrementAndGet();
        }
        if (data != null)
        {
            queue.offer(data);
        }
        if (left % count == 0)
        {
            pipeline.onNext(queue, runnerMode);
        }
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onComplete(Object result, RunnerMode runnerMode)
    {
        // TODO Auto-generated method stub
        
    }
    
}
