package com.jfireframework.context.event.impl;

import javax.annotation.PostConstruct;
import com.jfireframework.eventbus.bus.impl.IoEventBus;

public class IoEventPoster extends AbstractEventPoster
{
    private int  coreWorker = Runtime.getRuntime().availableProcessors();
    private int  maxWorker  = 100;
    private long waittime   = 60 * 1000;
    
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init()
    {
        eventBus = new IoEventBus(coreWorker, maxWorker, waittime);
        if (events != null)
        {
            eventBus.register(events.toArray(new Class[events.size()]));
        }
    }
}
