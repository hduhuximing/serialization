package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.util.RunnerMode;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.AsyncReadHandler;

public class ReciveHandler implements EventHandler<AsyncReadHandler>
{
    
    @Override
    public Object handle(AsyncReadHandler readHandler, RunnerMode runnerMode)
    {
        readHandler.asyncHandle();
        return null;
    }
    
}
