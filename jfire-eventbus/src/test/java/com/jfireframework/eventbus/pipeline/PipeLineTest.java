package com.jfireframework.eventbus.pipeline;

import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.ComputationEventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;
import com.jfireframework.eventbus.util.extra.NumberReduceOp;
import com.jfireframework.eventbus.util.extra.SingleAwaitOp;

public class PipeLineTest
{
    EventHandler<String> handler = new EventHandler<String>() {
                                     @Override
                                     public Object handle(String data, RunnerMode runnerMode)
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
                                     public Object handle(String data, RunnerMode runnerMode)
                                     {
                                         throw new NullPointerException();
                                     }
                                 };
    
    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        EventBus eventBus = new ComputationEventBus();
        EventHelper.register(PipeLineEvent.class);
        SingleAwaitOp singleAwaitOp = new SingleAwaitOp();
        Pipeline pipeLine = eventBus.pipeline()//
                .add(Operators.work(PipeLineEvent.one, handler, "one"))//
                .add(Operators.work(PipeLineEvent.two, handler, "two"))//
                .add(Operators.work(PipeLineEvent.three, handler, "three")).add(singleAwaitOp);
        pipeLine.start();
        singleAwaitOp.await();
        System.out.println("结束");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test2()
    {
        EventBus eventBus = new ComputationEventBus();
        EventHelper.register(PipeLineEvent.class);
        SingleAwaitOp singleAwaitOp = new SingleAwaitOp();
        Pipeline pipeLine = eventBus.pipeline()//
                .add(Operators.work(PipeLineEvent.one, handler, "one"))//
                .add(Operators.work(PipeLineEvent.two, handler, "two"))//
                .add(Operators.work(PipeLineEvent.four, handle2, "four"))//
                .add(Operators.work(PipeLineEvent.three, handler, "three"))//
                .add(singleAwaitOp);
        pipeLine.start();
        singleAwaitOp.await();
        System.out.println(singleAwaitOp.getE());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test3()
    {
        EventBus eventBus = new ComputationEventBus();
        EventHelper.register(PipeLineEvent.class);
        SingleAwaitOp op = new SingleAwaitOp();
        Pipeline pipeline = eventBus.pipeline()//
                .add(
                        Operators.work(
                                PipeLineEvent.one, new EventHandler<String>() {
                                    
                                    @Override
                                    public Object handle(String data, RunnerMode runnerMode)
                                    {
                                        System.out.println(data + "字符");
                                        return data;
                                    }
                                }, "12"
                        )
                ).add(
                        Operators.map(
                                new MapOp<String>() {
                                    
                                    @Override
                                    public Object map(String data)
                                    {
                                        System.out.println(data);
                                        return Integer.valueOf(data);
                                    }
                                }
                        )
                ).add(
                        Operators.work(
                                PipeLineEvent.one, new EventHandler<Integer>() {
                                    
                                    @Override
                                    public Object handle(Integer data, RunnerMode runnerMode)
                                    {
                                        System.out.println(data);
                                        return null;
                                    }
                                }
                        )
                ).add(op);
        pipeline.start();
        op.await();
        op.getE().printStackTrace();
        System.out.println(op.getE());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test4()
    {
        EventBus eventBus = new ComputationEventBus();
        EventHelper.register(PipeLineEvent.class);
        SingleAwaitOp op = new SingleAwaitOp();
        Pipeline pipeline = eventBus.pipeline()//
                .add(
                        Operators.work(
                                PipeLineEvent.one, new EventHandler<Void>() {
                                    
                                    @Override
                                    public Object handle(Void data, RunnerMode eventBus)
                                    {
                                        return new String[] { "1", "2" };
                                    }
                                }
                        )
                )// 投递一个字符串数组
                .add(Operators.from())// 从数组遍历，每一个数组元素作为下一个环节的数据提供
                .add(
                        Operators.map(
                                new MapOp<String>() {
                                    
                                    @Override
                                    public Object map(String data)
                                    {
                                        return Integer.valueOf(data);
                                    }
                                }
                        )
                )// 将收到的字符串数据转化为数字提供给下一个处理器
                .add(new NumberReduceOp(2))// 将收到的Integer类型数据存储在并发的Queue中，如果被调用两次后，将Queue中的数据传递下一个处理器
                .add(
                        Operators.work(
                                PipeLineEvent.one, new EventHandler<Queue<Integer>>() {
                                    
                                    @Override
                                    public Object handle(Queue<Integer> data, RunnerMode runnerMode)
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
                        )
                ).add(op);// 使用上一个环节提供的数据，也就是Queue，进行逻辑处理
        pipeline.start();
        op.await();
        System.out.println(op.getE());
        Assert.assertEquals(3, op.getResult());
    }
    
    @Test
    public void test5()
    {
        EventBus eventBus = new ComputationEventBus();
        EventHelper.register(PipeLineEvent.class);
        SingleAwaitOp op = new SingleAwaitOp();
        Pipeline pipeline = eventBus.pipeline()//
                .add(
                        Operators.work(
                                PipeLineEvent.one, new EventHandler<Void>() {
                                    
                                    @Override
                                    public Object handle(Void data, RunnerMode runnerMode)
                                    {
                                        System.out.println(Thread.currentThread().getName() + "创建原始数据");
                                        return "1";
                                    }
                                }
                        )
                ).add(
                        Operators.distribute(
                                new OperatorData(
                                        PipeLineEvent.one, new EventHandler<String>() {
                                            
                                            @Override
                                            public Object handle(String data, RunnerMode runnerMode)
                                            {
                                                System.out.println(Thread.currentThread().getName() + ":" + data);
                                                return data;
                                            }
                                        }
                                ), new OperatorData(
                                        PipeLineEvent.one, new EventHandler<String>() {
                                            
                                            @Override
                                            public Object handle(String data, RunnerMode runnerMode)
                                            {
                                                System.out.println(Thread.currentThread().getName() + "转换数字：" + Integer.valueOf(data));
                                                return Integer.valueOf(data);
                                            }
                                        }
                                )
                        )
                ).add(
                        Operators.work(
                                PipeLineEvent.one, new EventHandler<Object>() {
                                            
                                    @Override
                                    public Object handle(Object data, RunnerMode runnerMode)
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
                        )
                ).add(new NumberReduceOp(2)).add(op);
        pipeline.start();
        op.await();
    }
}
