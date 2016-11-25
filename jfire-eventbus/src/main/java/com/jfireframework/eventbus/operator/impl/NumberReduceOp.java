package com.jfireframework.eventbus.operator.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

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
    public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
    {
        int left = total.incrementAndGet();
        if (left < 0)
        {
            total.set(0);
            left = total.incrementAndGet();
        }
        queue.offer(data);
        if (left % count == 0)
        {
            pipeline.onCompleted(queue, runnerMode);
        }
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        // TODO Auto-generated method stub
        
    }
    
}
