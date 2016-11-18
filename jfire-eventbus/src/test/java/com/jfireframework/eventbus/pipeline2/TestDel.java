package com.jfireframework.eventbus.pipeline2;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class TestDel
{
    @Test
    public void test()
    {
        EventBus eventBus = new CalculateEventBus();
        eventBus.addHandler(new OneHandler());
        eventBus.addHandler(new Twohandler());
        eventBus.addHandler(new ThreeHandler());
        eventBus.start();
        Pipeline pipeline = eventBus.pipeline();
        pipeline.add("第一个", DeliEvent.one);
        pipeline.add(DeliEvent.two);
        pipeline.add(DeliEvent.three);
        pipeline.add("重新使用自己的数据", DeliEvent.one);
        pipeline.start();
        pipeline.await();
    }
}
