package com.jfireframework.eventbus.pipeline2;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class ThreeHandler implements EventHandler<DeliEvent, String>
{
    
    @Override
    public int getOrder()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public Object handle(String data, EventBus eventBus)
    {
        System.out.println(data);
        return null;
    }
    
    @Override
    public DeliEvent interest()
    {
        return DeliEvent.three;
    }
    
}
