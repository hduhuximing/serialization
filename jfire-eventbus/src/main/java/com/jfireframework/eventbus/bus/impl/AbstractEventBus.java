package com.jfireframework.eventbus.bus.impl;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
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
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.pipeline.impl.HeadPipeline;
import com.jfireframework.eventbus.util.EventHelper;

public abstract class AbstractEventBus implements EventBus
{
    protected final MPMCQueue<EventContext<?>>                                  eventQueue          = new MPMCQueue<EventContext<?>>();
    protected final IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap         = new IdentityHashMap<Enum<? extends EventConfig>, EventExecutor>();
    private final IdentityHashMap<Class<?>, EventExecutor>                      typeSerialMap       = new IdentityHashMap<Class<?>, EventExecutor>();
    private final IdentityHashMap<Class<?>, EventExecutor>                      typeRowKeySerialMap = new IdentityHashMap<Class<?>, EventExecutor>();
    private final IdentityHashMap<Class<?>, EventExecutor>                      readWriteMap        = new IdentityHashMap<Class<?>, EventExecutor>();
    protected static final Logger                                               LOGGER              = ConsoleLogFactory.getLogger();
    
    @SuppressWarnings("unchecked")
    public void register(Class<? extends Enum<? extends EventConfig>>... ckasses)
    {
        for (Class<? extends Enum<? extends EventConfig>> each : ckasses)
        {
            for (Enum<?> event : ReflectUtil.getAllEnumInstances(each).values())
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
                executorMap.put((Enum<? extends EventConfig>) event, executor);
            }
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, EventHandler<?> handler)
    {
        EventHelper.checkParallelLevel(event);
        EventConfig config = (EventConfig) event;
        if (config.parallelLevel() == ParallelLevel.RW_EVENT_READ)
        {
            EventContext<T> eventContext = new ReadWriteEventContextImpl(ReadWriteEventContext.READ, data, event, handler, executorMap.get(event), this);
            post(eventContext);
            return eventContext;
        }
        else if (config.parallelLevel() == ParallelLevel.RW_EVENT_WRITE)
        {
            EventContext<T> eventContext = new ReadWriteEventContextImpl(ReadWriteEventContext.WRITE, data, event, handler, executorMap.get(event), this);
            post(eventContext);
            return eventContext;
        }
        else
        {
            EventContext<T> eventContext = new NormalEventContext(data, event, handler, executorMap.get(event), this);
            post(eventContext);
            return eventContext;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        EventHelper.checkParallelLevel(event, rowkey);
        EventContext<T> eventContext = new RowEventContextImpl(data, event, handler, executorMap.get(event), this, rowkey);
        post(eventContext);
        return eventContext;
    }
    
    @Override
    public void post(EventContext<?> eventContext)
    {
        eventQueue.offerAndSignal(eventContext);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler)
    {
        EventHelper.checkParallelLevel(event, rowkey);
        EventContext<T> eventContext = new RowEventContextImpl(data, event, handler, executorMap.get(event), this, rowkey);
        eventContext.executor().handle(eventContext, this);
        eventContext.await();
        return eventContext;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, EventHandler<?> handle)
    {
        EventHelper.checkParallelLevel(event);
        EventContext<T> eventContext = new NormalEventContext(data, event, handle, executorMap.get(event), this);
        eventContext.executor().handle(eventContext, this);
        eventContext.await();
        return eventContext;
    }
    
    @Override
    public Pipeline pipeline()
    {
        return new HeadPipeline(this, executorMap);
    }
    
}
