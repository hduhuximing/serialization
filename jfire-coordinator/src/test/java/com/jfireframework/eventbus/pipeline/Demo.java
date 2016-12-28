package com.jfireframework.eventbus.pipeline;

import java.util.concurrent.locks.LockSupport;
import org.junit.Test;
import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.bus.CoordinatorBus;
import com.jfireframework.coordinator.bus.CoordinatorBuses;
import com.jfireframework.coordinator.bus.impl.ComputationCoordinatorBus;
import com.jfireframework.coordinator.pipeline.InternalPipeline;
import com.jfireframework.coordinator.pipeline.Pipeline;
import com.jfireframework.coordinator.util.CoordinatorHelper;
import com.jfireframework.coordinator.util.RunnerMode;

public class Demo
{
    @Test
    public void test()
    {
        CoordinatorHandler<String> handler = new CoordinatorHandler<String>() {
            
            @Override
            public Object handle(String data, RunnerMode runnerMode)
            {
                System.out.println(Thread.currentThread().getName() + ":" + data);
                return Integer.valueOf(data);
            }
        };
        CoordinatorHandler<Integer> handler2 = new CoordinatorHandler<Integer>() {
            
            @Override
            public Object handle(Integer data, RunnerMode runnerMode)
            {
                System.out.println(Thread.currentThread().getName() + ":" + (data + 1));
                return "";
            }
        };
        
        final CoordinatorBus eventBus = new ComputationCoordinatorBus();
        CoordinatorHelper.register(PipeLineEvent.class);
        Pipeline pipeline = eventBus.pipeline();
        pipeline = pipeline//
                .next(PipeLineEvent.one, handler, "1", "")//
                .switchTo(CoordinatorBuses.computation())//
                .next(PipeLineEvent.one, handler2, InternalPipeline.USE_UPSTREAM_RESULT, "");
        pipeline.start();
        LockSupport.parkNanos(1000000000000000l);
    }
}
