package com.jfireframework.eventbus.util;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.eventcontext.impl.NormalEventContext;
import com.jfireframework.eventbus.eventcontext.impl.ReadWriteEventContextImpl;
import com.jfireframework.eventbus.eventcontext.impl.RowEventContextImpl;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.executor.EventSerialHandlerExecutor;
import com.jfireframework.eventbus.executor.ParallelHandlerExecutor;
import com.jfireframework.eventbus.executor.ReadWriteExecutor;
import com.jfireframework.eventbus.executor.RowKeyHandlerExecutor;
import com.jfireframework.eventbus.executor.TypeRowKeySerialHandlerExecutor;
import com.jfireframework.eventbus.executor.TypeSerialHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class EventHelper
{
    protected static final IdentityHashMap<Enum<?>, EventExecutor> executorMap         = new IdentityHashMap<Enum<?>, EventExecutor>();
    private static final IdentityHashMap<Class<?>, EventExecutor>  typeSerialMap       = new IdentityHashMap<Class<?>, EventExecutor>();
    private static final IdentityHashMap<Class<?>, EventExecutor>  typeRowKeySerialMap = new IdentityHashMap<Class<?>, EventExecutor>();
    private static final IdentityHashMap<Class<?>, EventExecutor>  readWriteMap        = new IdentityHashMap<Class<?>, EventExecutor>();
    static
    {
        register(SwitchRunnerModeEvent.class);
    }
    
    public static void register(Class<? extends Enum<? extends EventConfig>>... ckasses)
    {
        for (Class<? extends Enum<? extends EventConfig>> each : ckasses)
        {
            register(each);
        }
    }
    
    public static void register(Class<? extends Enum<? extends EventConfig>> ckass)
    {
        for (Enum<?> event : ReflectUtil.getAllEnumInstances(ckass).values())
        {
            EventExecutor executor;
            switch (((EventConfig) event).parallelLevel())
            {
                case PAEALLEL:
                    executor = new ParallelHandlerExecutor();
                    break;
                case ROWKEY_SERIAL:
                    executor = new RowKeyHandlerExecutor();
                    break;
                case EVENT_SERIAL:
                    executor = new EventSerialHandlerExecutor();
                    break;
                case TYPE_SERIAL:
                    executor = typeSerialMap.get(event.getClass());
                    if (executor == null)
                    {
                        executor = new TypeSerialHandlerExecutor();
                        typeSerialMap.put(event.getClass(), executor);
                    }
                    break;
                case TYPE_ROWKEY_SERIAL:
                    executor = typeRowKeySerialMap.get(event.getClass());
                    if (executor == null)
                    {
                        executor = new TypeRowKeySerialHandlerExecutor();
                        typeRowKeySerialMap.put(event.getClass(), executor);
                    }
                    break;
                case RW_EVENT_READ:
                    // 直接走到下面，因为两个的逻辑是一样的
                    ;
                case RW_EVENT_WRITE:
                    executor = readWriteMap.get(event.getClass());
                    if (executor == null)
                    {
                        executor = new ReadWriteExecutor();
                        readWriteMap.put(event.getClass(), executor);
                    }
                    break;
                default:
                    throw new NullPointerException("事件" + event + "存在异常");
            }
            executorMap.put(event, executor);
        }
    }
    
    public static EventExecutor findExecutor(Enum<? extends EventConfig> event)
    {
        EventExecutor executor = executorMap.get(event);
        if (executor == null)
        {
            throw new NullPointerException();
        }
        return executor;
    }
    
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
    public static void handle(EventContext<?> eventContext, RunnerMode runnerMode)
    {
        Object result = null;
        try
        {
            Object data = eventContext.getEventData();
            EventHandler handler = eventContext.eventHandler();
            result = handler.handle(data, runnerMode);
            eventContext.setResult(result);
        }
        catch (Throwable e)
        {
            eventContext.setThrowable(e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static EventContext<?> build(RunnerMode runnerMode, Enum<? extends EventConfig> event, EventHandler<?> handler, Object data)
    {
        EventHelper.checkParallelLevel(event);
        EventConfig config = (EventConfig) event;
        if (config.parallelLevel() == ParallelLevel.RW_EVENT_READ)
        {
            return new ReadWriteEventContextImpl(runnerMode, ReadWriteEventContext.READ, data, handler, findExecutor(event));
        }
        else if (config.parallelLevel() == ParallelLevel.RW_EVENT_WRITE)
        {
            return new ReadWriteEventContextImpl(runnerMode, ReadWriteEventContext.WRITE, data, handler, findExecutor(event));
        }
        else
        {
            return new NormalEventContext(runnerMode, data, handler, findExecutor(event));
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static EventContext<?> build(RunnerMode runnerMode, Enum<? extends EventConfig> event, EventHandler<?> handler, Object data, Object rowkey)
    {
        EventHelper.checkParallelLevel(event, rowkey);
        return new RowEventContextImpl(runnerMode, data, handler, findExecutor(event), rowkey);
    }
}
