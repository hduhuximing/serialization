package com.jfireframework.context.event;

import javax.annotation.PostConstruct;
import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.util.EventHelper;

public class EventRegisterHelper
{
    protected String eventPath;
    
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init()
    {
        
        String[] events = PackageScan.scan(eventPath);
        for (String each : events)
        {
            try
            {
                Class<?> ckass = Class.forName(each);
                if (Enum.class.isAssignableFrom(ckass) || EventConfig.class.isAssignableFrom(ckass))
                {
                    EventHelper.register((Class<? extends Enum<? extends EventConfig>>) ckass);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
}
