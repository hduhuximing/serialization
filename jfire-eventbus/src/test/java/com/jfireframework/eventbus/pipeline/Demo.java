package com.jfireframework.eventbus.pipeline;

import java.nio.channels.Pipe;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.EventBuses;
import com.jfireframework.eventbus.bus.impl.ComputationEventBus;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;

public class Demo
{
    @Test
    public void test()
    {
        EventHandler<String> handler = new EventHandler<String>() {
            
            @Override
            public Object handle(String data, RunnerMode runnerMode)
            {
                System.out.println(Thread.currentThread().getName() + ":" + data);
                return Integer.valueOf(data);
            }
        };
        EventHandler<Integer> handler2 = new EventHandler<Integer>() {
            
            @Override
            public Object handle(Integer data, RunnerMode runnerMode)
            {
                System.out.println(Thread.currentThread().getName() + ":" + (data + 1));
                return "";
            }
        };
        
        final EventBus eventBus = new ComputationEventBus();
        EventHelper.register(PipeLineEvent.class);
        Pipeline pipeline = eventBus.pipeline();
        pipeline = pipeline//
                .add(Operators.work(PipeLineEvent.one, handler, "1", ""))//
                .add(Operators.switchMode(EventBuses.computation()))//
                .add(Operators.work(PipeLineEvent.one, handler2, Pipeline.USE_UPSTREAM_RESULT, ""));
        pipeline.start();
        LockSupport.parkNanos(1000000000000000l);
    }
}
