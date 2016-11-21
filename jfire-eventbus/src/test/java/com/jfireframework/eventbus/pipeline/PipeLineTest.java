package com.jfireframework.eventbus.pipeline;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline.PipelineData;
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
                .add(PipeLineEvent.one, handler, "one")//
                .add(PipeLineEvent.two, handler, "two")//
                .add(PipeLineEvent.three, handler, "three");
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
                .add(PipeLineEvent.one, handler, "one")//
                .add(PipeLineEvent.two, handler, "two")//
                .add(PipeLineEvent.four, handle2, "four")//
                .add(PipeLineEvent.three, handler, "three");
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
        Pipeline pipeline = eventBus.pipeline()//
                .add(
                        PipeLineEvent.one, new EventHandler<String>() {
                            
                            @Override
                            public Object handle(String data, EventBus eventBus)
                            {
                                System.out.println(data + "字符");
                                return data;
                            }
                        }, "12", null
                ).conversion(
                        new MapConversion<String>() {
                            
                            @Override
                            protected Object conversie(String str)
                            {
                                return Integer.valueOf(str);
                            }
                        }
                ).add(
                        PipeLineEvent.one, new EventHandler<Integer>() {
                            
                            @Override
                            public Object handle(Integer data, EventBus eventBus)
                            {
                                System.out.println(data);
                                return null;
                            }
                        }, Pipeline.USE_UPSTREAM_RESULT, null
                );
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
                .add(
                        PipeLineEvent.one, new EventHandler<Void>() {
                            
                            @Override
                            public Object handle(Void data, EventBus eventBus)
                            {
                                return new String[] { "1", "2" };
                            }
                        }
                )// 投递一个字符串数组
                .conversion(new FromArray<String[]>())// 从数组遍历，每一个数组元素作为下一个环节的数据提供
                .conversion(
                        new MapConversion<String>() {
                            
                            @Override
                            protected Object conversie(String data)
                            {
                                return Integer.valueOf(data);
                            }
                        }
                )// 将收到的字符串数据转化为数字提供给下一个处理器
                .conversion(new NumberReduceConversion<Integer>(2))// 将收到的Integer类型数据存储在并发的Queue中，如果被调用两次后，将Queue中的数据传递下一个处理器
                .add(
                        PipeLineEvent.one, new EventHandler<Queue<Integer>>() {
                            
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
                        }
                );// 使用上一个环节提供的数据，也就是Queue，进行逻辑处理
        pipeline.start();
        pipeline.await();
        Assert.assertEquals(3, pipeline.getResult());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test5()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.register(PipeLineEvent.class);
        
        Pipeline pipeline = eventBus.pipeline()//
                .add(
                        PipeLineEvent.one, new EventHandler<Void>() {
                            
                            @Override
                            public Object handle(Void data, EventBus eventBus)
                            {
                                return "1";
                            }
                        }
                ).addAll(
                        new PipelineData(
                                PipeLineEvent.one, new EventHandler<String>() {
                                    
                                    @Override
                                    public Object handle(String data, EventBus eventBus)
                                    {
                                        System.out.println(data);
                                        return data;
                                    }
                                }
                        ), new PipelineData(
                                PipeLineEvent.one, new EventHandler<String>() {
                                    
                                    @Override
                                    public Object handle(String data, EventBus eventBus)
                                    {
                                        System.out.println("转换数字：" + Integer.valueOf(data));
                                        return Integer.valueOf(data);
                                    }
                                }
                        )
                ).add(
                        PipeLineEvent.one, new EventHandler<Object>() {
                                    
                            @Override
                            public Object handle(Object data, EventBus eventBus)
                            {
                                if (data instanceof String)
                                {
                                    System.out.println(Thread.currentThread().getName() + "收到字符串：" + (String) data);
                                }
                                else if (data instanceof Integer)
                                {
                                    System.out.println(Thread.currentThread().getName() + "收到数组：" + data);
                                }
                                else
                                {
                                    System.out.println("无法识别");
                                }
                                return data;
                            }
                        }
                );
        pipeline.start();
        pipeline.await();
    }
}
