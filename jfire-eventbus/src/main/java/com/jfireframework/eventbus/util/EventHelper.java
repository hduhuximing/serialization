package com.jfireframework.eventbus.util;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public class EventHelper
{
    public static void checkParallelLevel(Enum<? extends EventConfig> event, Object rowkey)
    {
        if (((EventConfig) event).parallelLevel() != ParallelLevel.ROWKEY_SERIAL && ((EventConfig) event).parallelLevel() != ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法只能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
    }
    
    public static void checkParallelLevel(Enum<? extends EventConfig> event)
    {
        if (((EventConfig) event).parallelLevel() == ParallelLevel.ROWKEY_SERIAL || ((EventConfig) event).parallelLevel() == ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法不能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        Object result = null;
        try
        {
            Object data = eventContext.getEventData();
            EventHandler handler = eventContext.eventHandler();
            result = handler.handle(data, eventBus);
        }
        catch (Throwable e)
        {
            eventContext.setThrowable(e);
        }
        finally
        {
            eventContext.signal(result);
        }
    }
    
}
