package com.jfireframework.eventbus.pipeline.conversion;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class NumberReduceConversion<E> implements Conversion<E>
{
    private final AtomicInteger            count;
    private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    
    public NumberReduceConversion(int sum)
    {
        count = new AtomicInteger(sum);
    }
    
    @Override
    public boolean conversie(E data, Pipeline pipeline)
    {
        if (data != null)
        {
            queue.offer(data);
        }
        if (count.decrementAndGet() == 0)
        {
            pipeline.onCompleted(queue);
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
