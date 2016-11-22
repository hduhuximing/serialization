package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.AsyncReadHandler;

public class ReciveHandler implements EventHandler<AsyncReadHandler>
{
    
    @Override
    public Object handle(AsyncReadHandler readHandler, EventBus eventBus)
    {
        readHandler.asyncHandle();
        return null;
    }
    
}
