package com.jfireframework.eventbus.pipeline;

import org.junit.Test;
import com.jfireframework.eventbus.FoutHandler;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;

public class PipeLineTest
{
    @Test
    public void test()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.addHandler(new OneHandler());
        eventBus.addHandler(new TwoHandler());
        eventBus.addHandler(new ThreeHandler());
        eventBus.start();
        Pipeline pipeLine = eventBus.pipeline();
        pipeLine.add("one", PipeLineEvent.one);
        pipeLine.add("two", PipeLineEvent.two);
        pipeLine.add("three", PipeLineEvent.three);
        pipeLine.start();
        pipeLine.await();
        System.out.println("结束");
    }
    
    @Test
    public void test2()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.addHandler(new OneHandler());
        eventBus.addHandler(new TwoHandler());
        eventBus.addHandler(new ThreeHandler());
        eventBus.addHandler(new FoutHandler());
        eventBus.start();
        Pipeline pipeLine = eventBus.pipeline();
        pipeLine.add("one", PipeLineEvent.one);
        pipeLine.add("two", PipeLineEvent.two);
        pipeLine.add("four", PipeLineEvent.four);
        pipeLine.add("three", PipeLineEvent.three);
        pipeLine.start();
        pipeLine.await();
        System.out.println(pipeLine.getThrowable());
    }
    
}
