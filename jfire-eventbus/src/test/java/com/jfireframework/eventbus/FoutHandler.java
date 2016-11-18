package com.jfireframework.eventbus;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.PipeLineEvent;

public class FoutHandler implements EventHandler<PipeLineEvent, String>
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
        throw new NullPointerException();
    }
    
    @Override
    public PipeLineEvent interest()
    {
        return PipeLineEvent.four;
    }
    
}
