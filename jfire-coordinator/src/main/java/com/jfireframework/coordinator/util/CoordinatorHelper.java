package com.jfireframework.coordinator.util;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.api.ParallelLevel;
import com.jfireframework.coordinator.executor.EventExecutor;
import com.jfireframework.coordinator.executor.EventSerialHandlerExecutor;
import com.jfireframework.coordinator.executor.ParallelHandlerExecutor;
import com.jfireframework.coordinator.executor.ReadWriteExecutor;
import com.jfireframework.coordinator.executor.RowKeyHandlerExecutor;
import com.jfireframework.coordinator.executor.TypeRowKeySerialHandlerExecutor;
import com.jfireframework.coordinator.executor.TypeSerialHandlerExecutor;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.handlercontext.ReadWriteCoordinatorContext;
import com.jfireframework.coordinator.handlercontext.impl.NormalCoordinatorContext;
import com.jfireframework.coordinator.handlercontext.impl.ReadWriteCoordinatorContextImpl;
import com.jfireframework.coordinator.handlercontext.impl.RowCoordinatorContextImpl;
import com.jfireframework.coordinator.util.RunnerMode.ThreadMode;

public class CoordinatorHelper
{
    protected static final IdentityHashMap<Enum<?>, EventExecutor> executorMap         = new IdentityHashMap<Enum<?>, EventExecutor>();
    private static final IdentityHashMap<Class<?>, EventExecutor>  typeSerialMap       = new IdentityHashMap<Class<?>, EventExecutor>();
    private static final IdentityHashMap<Class<?>, EventExecutor>  typeRowKeySerialMap = new IdentityHashMap<Class<?>, EventExecutor>();
    private static final IdentityHashMap<Class<?>, EventExecutor>  readWriteMap        = new IdentityHashMap<Class<?>, EventExecutor>();
    static
    {
        register(DefaultCoordinator.class);
    }
    
    public static void register(Class<? extends Enum<? extends CoordinatorConfig>>... ckasses)
    {
        for (Class<? extends Enum<? extends CoordinatorConfig>> each : ckasses)
        {
            register(each);
        }
    }
    
    public static void register(Class<? extends Enum<? extends CoordinatorConfig>> ckass)
    {
        for (Enum<?> event : ReflectUtil.getAllEnumInstances(ckass).values())
        {
            EventExecutor executor;
            switch (((CoordinatorConfig) event).parallelLevel())
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
    
    public static EventExecutor findExecutor(Enum<? extends CoordinatorConfig> event)
    {
        EventExecutor executor = executorMap.get(event);
        if (executor == null)
        {
            throw new NullPointerException("协调者配置:" + event.getClass().getName() + "不存在对应的执行器");
        }
        return executor;
    }
    
    public static void checkParallelLevel(Enum<? extends CoordinatorConfig> event, Object rowkey)
    {
        if (((CoordinatorConfig) event).parallelLevel() != ParallelLevel.ROWKEY_SERIAL && ((CoordinatorConfig) event).parallelLevel() != ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法只能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
    }
    
    public static void checkParallelLevel(Enum<? extends CoordinatorConfig> event)
    {
        if (((CoordinatorConfig) event).parallelLevel() == ParallelLevel.ROWKEY_SERIAL || ((CoordinatorConfig) event).parallelLevel() == ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法不能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void handle(CoordinatorContext<?> eventContext, RunnerMode runnerMode)
    {
        Object result = null;
        try
        {
            Object data = eventContext.getEventData();
            CoordinatorHandler handler = eventContext.eventHandler();
            result = handler.handle(data, runnerMode);
            eventContext.setResult(result);
        }
        catch (Throwable e)
        {
            eventContext.setThrowable(e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CoordinatorContext<?> build(RunnerMode runnerMode, Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data)
    {
        CoordinatorHelper.checkParallelLevel(event);
        CoordinatorConfig config = (CoordinatorConfig) event;
        if (config.parallelLevel() == ParallelLevel.RW_EVENT_READ)
        {
            return new ReadWriteCoordinatorContextImpl(runnerMode, ReadWriteCoordinatorContext.READ, data, handler, findExecutor(event));
        }
        else if (config.parallelLevel() == ParallelLevel.RW_EVENT_WRITE)
        {
            return new ReadWriteCoordinatorContextImpl(runnerMode, ReadWriteCoordinatorContext.WRITE, data, handler, findExecutor(event));
        }
        else
        {
            return new NormalCoordinatorContext(runnerMode, data, handler, findExecutor(event));
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CoordinatorContext<?> build(RunnerMode runnerMode, Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data, Object rowkey)
    {
        CoordinatorHelper.checkParallelLevel(event, rowkey);
        return new RowCoordinatorContextImpl(runnerMode, data, handler, findExecutor(event), rowkey);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> CoordinatorContext<T> sync(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data, Object rowkey)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentThread, null);
        CoordinatorContext<T> eventContext = (CoordinatorContext<T>) CoordinatorHelper.build(runnerMode, event, handler, data, rowkey);
        eventContext.run();
        eventContext.await();
        return eventContext;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> CoordinatorContext<T> sync(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data)
    {
        RunnerMode runnerMode = new RunnerMode(ThreadMode.currentThread, null);
        CoordinatorContext<T> eventContext = (CoordinatorContext<T>) CoordinatorHelper.build(runnerMode, event, handler, data);
        eventContext.run();
        eventContext.await();
        return eventContext;
    }
    
}
