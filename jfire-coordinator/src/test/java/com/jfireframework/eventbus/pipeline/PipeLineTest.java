package com.jfireframework.eventbus.pipeline;

import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.bus.CoordinatorBus;
import com.jfireframework.coordinator.bus.impl.ComputationCoordinatorBus;
import com.jfireframework.coordinator.operator.MapOp;
import com.jfireframework.coordinator.operator.OperatorData;
import com.jfireframework.coordinator.operator.TransferOp;
import com.jfireframework.coordinator.operator.impl.NumberReduceOp;
import com.jfireframework.coordinator.operator.impl.SingleAwaitOp;
import com.jfireframework.coordinator.pipeline.Pipeline;
import com.jfireframework.coordinator.pipeline.impl.DefaultPipeline;
import com.jfireframework.coordinator.util.CoordinatorHelper;
import com.jfireframework.coordinator.util.RunnerMode;

public class PipeLineTest
{
    CoordinatorHandler<String> handler = new CoordinatorHandler<String>() {
                                           @Override
                                           public Object handle(String data, RunnerMode runnerMode)
                                           {
                                               try
                                               {
                                                   Thread.sleep(1000);
                                               }
                                               catch (InterruptedException e)
                                               {
                                                   // TODO Auto-generated catch
                                                   // block
                                                   e.printStackTrace();
                                               }
                                               System.out.println(data);
                                               return data;
                                           }
                                       };
    CoordinatorHandler<String> handle2 = new CoordinatorHandler<String>() {
                                           
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
        CoordinatorBus eventBus = new ComputationCoordinatorBus();
        CoordinatorHelper.register(PipeLineEvent.class);
        SingleAwaitOp singleAwaitOp = new SingleAwaitOp();
        Pipeline pipeLine = eventBus.pipeline()//
                .next(PipeLineEvent.one, handler, "one")//
                .next(PipeLineEvent.two, handler, "two")//
                .next(PipeLineEvent.three, handler, "three").add(singleAwaitOp);
        pipeLine.start();
        singleAwaitOp.await();
        System.out.println("结束");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test2()
    {
        CoordinatorBus eventBus = new ComputationCoordinatorBus();
        CoordinatorHelper.register(PipeLineEvent.class);
        SingleAwaitOp singleAwaitOp = new SingleAwaitOp();
        Pipeline pipeLine = eventBus.pipeline()//
                .next(PipeLineEvent.one, handler, "one")//
                .next(PipeLineEvent.two, handler, "two")//
                .next(PipeLineEvent.four, handle2, "four")//
                .next(PipeLineEvent.three, handler, "three")//
                .add(singleAwaitOp);
        pipeLine.start();
        singleAwaitOp.await();
        System.out.println(singleAwaitOp.getE());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test3()
    {
        CoordinatorBus eventBus = new ComputationCoordinatorBus();
        CoordinatorHelper.register(PipeLineEvent.class);
        SingleAwaitOp op = new SingleAwaitOp();
        Pipeline pipeline = eventBus.pipeline()//
                .next(PipeLineEvent.one, new CoordinatorHandler<String>() {
                    
                    @Override
                    public Object handle(String data, RunnerMode runnerMode)
                    {
                        System.out.println(data + "字符");
                        return data;
                    }
                }, "12").map(new MapOp<String>() {
                    
                    @Override
                    public Object map(String data)
                    {
                        System.out.println(data);
                        return Integer.valueOf(data);
                    }
                }).next(PipeLineEvent.one, new CoordinatorHandler<Integer>() {
                    
                    @Override
                    public Object handle(Integer data, RunnerMode runnerMode)
                    {
                        System.out.println(data + 1);
                        return null;
                    }
                }).add(op);
        pipeline.start();
        op.await();
        // op.getE().printStackTrace();
        // System.out.println(op.getE());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test4()
    {
        CoordinatorBus eventBus = new ComputationCoordinatorBus();
        CoordinatorHelper.register(PipeLineEvent.class);
        SingleAwaitOp op = new SingleAwaitOp();
        Pipeline pipeline = DefaultPipeline.from(new String[] { "1", "2" }) // 从数组遍历，每一个数组元素作为下一个环节的数据提供
                .switchTo(eventBus)// 投递一个字符串数组
                .map(new MapOp<String>() {
                    
                    @Override
                    public Object map(String data)
                    {
                        return Integer.valueOf(data);
                    }
                }
                
                )// 将收到的字符串数据转化为数字提供给下一个处理器
                .add(new NumberReduceOp(2))// 将收到的Integer类型数据存储在并发的Queue中，如果被调用两次后，将Queue中的数据传递下一个处理器
                .next(PipeLineEvent.one, new CoordinatorHandler<Queue<Integer>>() {
                    
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
                
                ).add(op);// 使用上一个环节提供的数据，也就是Queue，进行逻辑处理
        pipeline.start();
        op.await();
        System.out.println(op.getE());
        Assert.assertEquals(3, op.getResult());
    }
    
    @Test
    public void test5()
    {
        CoordinatorBus eventBus = new ComputationCoordinatorBus();
        CoordinatorHelper.register(PipeLineEvent.class);
        SingleAwaitOp op = new SingleAwaitOp();
        Pipeline pipeline = eventBus.pipeline()//
                .next(PipeLineEvent.one, new CoordinatorHandler<Void>() {
                    
                    @Override
                    public Object handle(Void data, RunnerMode runnerMode)
                    {
                        System.out.println(Thread.currentThread().getName() + "创建原始数据");
                        return "1";
                    }
                }
                
                ).distribute(new OperatorData(PipeLineEvent.one, new CoordinatorHandler<String>() {
                    
                    @Override
                    public Object handle(String data, RunnerMode runnerMode)
                    {
                        System.out.println(Thread.currentThread().getName() + ":" + data);
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return data;
                    }
                }), new OperatorData(PipeLineEvent.one, new CoordinatorHandler<String>() {
                    
                    @Override
                    public Object handle(String data, RunnerMode runnerMode)
                    {
                        System.out.println(Thread.currentThread().getName() + "转换数字：" + Integer.valueOf(data));
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return Integer.valueOf(data);
                    }
                })).next(PipeLineEvent.one, new CoordinatorHandler<Object>() {
                    
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
                
                ).add(new NumberReduceOp(2)).add(op);
        pipeline.start();
        op.await();
    }
    
    @Test
    public void test6()
    {
        TransferOp op = new TransferOp() {
            
            @Override
            public Pipeline transfer(Pipeline pipeline)
            {
                return pipeline.next(new CoordinatorHandler<String>() {
                    
                    @Override
                    public Object handle(String data, RunnerMode runnerMode)
                    {
                        System.out.println(data);
                        return 1;
                    }
                })//
                        .next(new CoordinatorHandler<Integer>() {
                            
                            @Override
                            public Object handle(Integer data, RunnerMode runnerMode)
                            {
                                System.out.println(data);
                                return null;
                            }
                        });
            }
        };
        DefaultPipeline.create()//
                .next(new CoordinatorHandler<String>() {
                    
                    @Override
                    public Object handle(String data, RunnerMode runnerMode)
                    {
                        return "nihao";
                    }
                })//
                .compose(op).start();
        DefaultPipeline.create().next(new CoordinatorHandler<String>() {
            
            @Override
            public Object handle(String data, RunnerMode runnerMode)
            {
                return "hello";
            }
        }).compose(op).start();
    }
    
    /**
     * 测试from
     * @throws InterruptedException 
     */
    @Test
    public void test7() throws InterruptedException
    {
        DefaultPipeline.create()//
                .from()//
                .next(new CoordinatorHandler<Integer>() {
                    
                    @Override
                    public Object handle(Integer data, RunnerMode runnerMode)
                    {
                        System.out.println(Thread.currentThread() + "," + data);
                        return null;
                    }
                }).start(new int[] { 1, 2 });
        Thread.sleep(1000);
    }
}
