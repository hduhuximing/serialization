package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class ThreeHandler implements EventHandler<PipeLineEvent, String>
{
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
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
    
    @Override
    public PipeLineEvent interest()
    {
        return PipeLineEvent.three;
    }
    
}
