package com.jfireframework.eventbus.pipeline;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class PipeLineTest
{
    EventHandler<String> handler = new EventHandler<String>() {
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
    EventHandler<String> handle2 = new EventHandler<String>() {
                                     
                                     @Override
                                     public Object handle(String data, EventBus eventBus)
                                     {
                                         throw new NullPointerException();
                                     }
                                 };
    
    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.register(PipeLineEvent.class);
        Pipeline pipeLine = eventBus.pipeline()//
                .add("one", PipeLineEvent.one, handler)//
                .add("two", PipeLineEvent.two, handler)//
                .add("three", PipeLineEvent.three, handler);
        pipeLine.start();
        pipeLine.await();
        System.out.println("结束");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test2()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.register(PipeLineEvent.class);
        Pipeline pipeLine = eventBus.pipeline()//
                .add("one", PipeLineEvent.one, handler)//
                .add("two", PipeLineEvent.two, handler)//
                .add("four", PipeLineEvent.four, handle2)//
                .add("three", PipeLineEvent.three, handle2);
        pipeLine.start();
        pipeLine.await();
        System.out.println(pipeLine.getThrowable());
    }
    
}
