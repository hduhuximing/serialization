package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.util.RunnerMode;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.AsyncReadHandler;

public class ReciveHandler implements CoordinatorHandler<AsyncReadHandler>
{
    
    @Override
    public Object handle(AsyncReadHandler readHandler, RunnerMode runnerMode)
    {
        readHandler.asyncHandle();
        return null;
    }
    
}
