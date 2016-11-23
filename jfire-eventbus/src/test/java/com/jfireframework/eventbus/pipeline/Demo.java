package com.jfireframework.eventbus.pipeline;

import java.nio.channels.Pipe;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class Demo
{
    @Test
    public void test()
    {
        final EventBus eventBus = new CalculateEventBus();
        eventBus.register(PipeLineEvent.class);
        Pipeline pipeline = eventBus.pipeline();
        Operator operator = new Operator() {
            
            @Override
            public void work(EventContext<?> eventContext, EventBus eventBus)
            {
                eventContext.executor().handle(eventContext, eventBus);
            }
            
            @Override
            public Object rowKey()
            {
                return null;
            }
            
            @Override
            public ParallelLevel level()
            {
                return ParallelLevel.PAEALLEL;
            }
            
            @Override
            public EventHandler<?> handler()
            {
                return new EventHandler<String>() {
                    @Override
                    public Object handle(String data, EventBus eventBus)
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println(data);
                        return data;
                    }
                };
            }
            
            @Override
            public EventExecutor executor()
            {
                return eventBus.findExecutor(PipeLineEvent.one);
            }
            
            @Override
            public Object eventData()
            {
                return "one";
            }
        };
        pipeline = pipeline.addOperator(operator);
        pipeline.start();
        LockSupport.parkNanos(1000000000000000l);
    }
}
