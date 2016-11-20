package com.jfireframework.eventbus.pipeline;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.conversion.FromArray;
import com.jfireframework.eventbus.pipeline.conversion.MapConversion;
import com.jfireframework.eventbus.pipeline.conversion.NumberReduceConversion;

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
    
    @SuppressWarnings("unchecked")
    @Test
    public void test3()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.register(PipeLineEvent.class);
        Pipeline pipeline = eventBus.pipeline().add("12", PipeLineEvent.one, new EventHandler<String>() {
            
            @Override
            public Object handle(String data, EventBus eventBus)
            {
                System.out.println(data + "字符");
                return data;
            }
        }).conversion(new MapConversion<String>() {
            
            @Override
            protected Object conversie(String str)
            {
                return Integer.valueOf(str);
            }
        }).add(PipeLineEvent.one, new EventHandler<Integer>() {
            
            @Override
            public Object handle(Integer data, EventBus eventBus)
            {
                System.out.println(data);
                return null;
            }
        });
        pipeline.start();
        pipeline.await();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test4()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.register(PipeLineEvent.class);
        Pipeline pipeline = eventBus.pipeline()//
                .add(PipeLineEvent.one, new EventHandler<Void>() {
                    
                    @Override
                    public Object handle(Void data, EventBus eventBus)
                    {
                        return new String[] { "1", "2" };
                    }
                })// 投递一个字符串数组
                .conversion(new FromArray<String[]>())// 从数组遍历，每一个数组元素作为下一个环节的数据提供
                .conversion(new MapConversion<String>() {
                    
                    @Override
                    protected Object conversie(String data)
                    {
                        return Integer.valueOf(data);
                    }
                })// 将收到的字符串数据转化为数字提供给下一个处理器
                .conversion(new NumberReduceConversion<Integer>(2))// 将收到的Integer类型数据存储在并发的Queue中，如果被调用两次后，将Queue中的数据传递下一个处理器
                .add(PipeLineEvent.one, new EventHandler<Queue<Integer>>() {
                    
                    @Override
                    public Object handle(Queue<Integer> data, EventBus eventBus)
                    {
                        int sum = 0;
                        for (Integer each : data)
                        {
                            sum += each;
                        }
                        System.out.println(sum);
                        return sum;
                    }
                });// 使用上一个环节提供的数据，也就是Queue，进行逻辑处理
        pipeline.start();
        pipeline.await();
        Assert.assertEquals(3, pipeline.getResult());
        LockSupport.parkNanos(100000000);
    }
}
