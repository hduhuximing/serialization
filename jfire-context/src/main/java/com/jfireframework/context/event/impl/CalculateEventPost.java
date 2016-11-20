package com.jfireframework.context.event.impl;

import javax.annotation.PostConstruct;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;

public class CalculateEventPost extends AbstractEventPoster
{
    private int coreWorker = Runtime.getRuntime().availableProcessors();
    
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init()
    {
        eventBus = new CalculateEventBus(coreWorker);
        if (events != null)
        {
            eventBus.register(events.toArray(new Class[events.size()]));
        }
    }
}
