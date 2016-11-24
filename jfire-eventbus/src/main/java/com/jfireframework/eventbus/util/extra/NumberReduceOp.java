package com.jfireframework.eventbus.util.extra;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.eventbus.pipeline.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.RunnerMode;

public class NumberReduceOp implements Operator
{
    private AtomicInteger count;
    private Queue<Object> queue = new ConcurrentLinkedQueue<Object>();
    
    public NumberReduceOp(int count)
    {
        this.count = new AtomicInteger(count);
    }
    
    @Override
    public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
    {
        queue.offer(data);
        if (count.decrementAndGet() == 0)
        {
            pipeline.onCompleted(queue, runnerMode);
        }
    }
    
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        // TODO Auto-generated method stub
        
    }
    
}
