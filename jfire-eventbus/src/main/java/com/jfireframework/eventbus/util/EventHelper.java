package com.jfireframework.eventbus.util;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

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
    
}
