package com.jfireframework.eventbus.pipeline2;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class TestDel
{
    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.register(DeliEvent.class);
        Pipeline pipeline = eventBus.pipeline()//
                .add("第一个", DeliEvent.one, new EventHandler<String>() {
                    
                    @Override
                    public Object handle(String data, EventBus eventBus)
                    {
                        System.out.println(data);
                        return "这是第一个人传递的数据";
                    }
                })//
                .add(DeliEvent.two, new EventHandler<String>() {
                    
                    @Override
                    public Object handle(String data, EventBus eventBus)
                    {
                        System.out.println(data);
                        return "这是第二个人传递的";
                    }
                })//
                .add(DeliEvent.three, new EventHandler<String>() {
                    
                    @Override
                    public Object handle(String data, EventBus eventBus)
                    {
                        System.out.println(data);
                        return "这是第三个人传递的";
                    }
                })//
                .add("重新使用自己的数据", DeliEvent.one, new EventHandler<String>() {
                    
                    @Override
                    public Object handle(String data, EventBus eventBus)
                    {
                        System.out.println(data);
                        return null;
                    }
                });
        pipeline.start();
        pipeline.await();
    }
}
