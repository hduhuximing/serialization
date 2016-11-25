package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.EventBuses;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.eventcontext.impl.NormalEventContext;
import com.jfireframework.eventbus.eventcontext.impl.ReadWriteEventContextImpl;
import com.jfireframework.eventbus.eventcontext.impl.RowEventContextImpl;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.operator.MapOp;
import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.operator.OperatorData;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;
import com.jfireframework.eventbus.util.RunnerMode.ThreadMode;
import com.jfireframework.eventbus.util.DefaultEvent;

public class DefaultPipeline extends BasePipeline
{
    
    public DefaultPipeline(Operator operator, Pipeline pred)
    {
        super(operator, pred);
    }
    
    public static Pipeline create()
    {
        return new HeadPipeline();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private EventContext<?> proxy(Pipeline pipeline, RunnerMode runnerMode, Enum<? extends EventConfig> event, EventHandler<?> handler, Object data, Object rowKey)
    {
        EventContext<?> eventContext;
        switch (((EventConfig) event).parallelLevel())
        {
            case PAEALLEL:
                eventContext = new NormalEventContextProxy(pipeline, runnerMode, data, handler, EventHelper.findExecutor(event));
                break;
            case EVENT_SERIAL:
                eventContext = new NormalEventContextProxy(pipeline, runnerMode, data, handler, EventHelper.findExecutor(event));
                break;
            case ROWKEY_SERIAL:
                if (rowKey == Pipeline.USE_UPSTREAM_RESULT)
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, data, handler, EventHelper.findExecutor(event));
                }
                else
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, rowKey, handler, EventHelper.findExecutor(event));
                }
                break;
            case RW_EVENT_READ:
                eventContext = new ReadWriteEventContextProxy(pipeline, runnerMode, ReadWriteEventContext.READ, data, handler, EventHelper.findExecutor(event));
                break;
            case RW_EVENT_WRITE:
                eventContext = new ReadWriteEventContextProxy(pipeline, runnerMode, ReadWriteEventContext.WRITE, data, handler, EventHelper.findExecutor(event));
                break;
            case TYPE_ROWKEY_SERIAL:
                if (rowKey == Pipeline.USE_UPSTREAM_RESULT)
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, data, handler, EventHelper.findExecutor(event));
                }
                else
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, rowKey, handler, EventHelper.findExecutor(event));
                }
                break;
            case TYPE_SERIAL:
                eventContext = new NormalEventContextProxy(pipeline, runnerMode, data, handler, EventHelper.findExecutor(event));
                break;
            default:
                throw new NullPointerException();
        }
        return eventContext;
    }
    
    static class ReadWriteEventContextProxy<T> extends ReadWriteEventContextImpl<T>
    {
        private final Pipeline pipeline;
        
        public ReadWriteEventContextProxy(Pipeline pipeline, RunnerMode runnerMode, int mode, Object eventData, EventHandler<?> handler, EventExecutor executor)
        {
            super(runnerMode, mode, eventData, handler, executor);
            this.pipeline = pipeline;
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            pipeline.onCompleted(result, runnerMode);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            pipeline.onError(e, runnerMode);
        }
    }
    
    static class RowEventContextProxy<T> extends RowEventContextImpl<T>
    {
        private final Pipeline pipeline;
        
        public RowEventContextProxy(Pipeline pipeline, RunnerMode runnerMode, Object eventData, Object rowkey, EventHandler<?> handler, EventExecutor executor)
        {
            super(runnerMode, eventData, handler, executor, rowkey);
            this.pipeline = pipeline;
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            pipeline.onCompleted(result, runnerMode);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            pipeline.onError(e, runnerMode);
        }
    }
    
    static class NormalEventContextProxy<T> extends NormalEventContext<T>
    {
        private final Pipeline pipeline;
        
        public NormalEventContextProxy(Pipeline pipeline, RunnerMode runnerMode, Object eventData, EventHandler<?> handler, EventExecutor executor)
        {
            super(runnerMode, eventData, handler, executor);
            this.pipeline = pipeline;
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            pipeline.onCompleted(result, runnerMode);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            pipeline.onError(e, runnerMode);
        }
    }
    
    @Override
    public Pipeline work(final Enum<? extends EventConfig> event, final EventHandler<?> handler, final Object eventData, final Object rowKey)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
            {
                if (eventData != Pipeline.USE_UPSTREAM_RESULT)
                {
                    data = eventData;
                }
                EventContext<?> eventContext = proxy(pipeline, runnerMode, event, handler, data, rowKey);
                eventContext.executor().handle(eventContext, runnerMode);
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
        };
        return add(operator);
    }
    
    @Override
    public Pipeline work(final Enum<? extends EventConfig> event, final EventHandler<?> handler, final Object eventData)
    {
        return work(event, handler, eventData, Pipeline.USE_UPSTREAM_RESULT);
    }
    
    @Override
    public Pipeline work(final Enum<? extends EventConfig> event, final EventHandler<?> handler)
    {
        return work(event, handler, Pipeline.USE_UPSTREAM_RESULT, Pipeline.USE_UPSTREAM_RESULT);
    }
    
    @Override
    public Pipeline work(EventHandler<?> handler)
    {
        return work(DefaultEvent.JUST_PAEALLEL_EVENT, handler, USE_UPSTREAM_RESULT, USE_UPSTREAM_RESULT);
    }
    
    private static final EventHandler<Object> switchHandler = new EventHandler<Object>() {
        
        @Override
        public Object handle(Object data, RunnerMode runnerMode)
        {
            return data;
        }
    };
    
    @Override
    public Pipeline switchMode(final EventBus eventBus)
    {
        final RunnerMode newRunnerMode = new RunnerMode(ThreadMode.currentEventbus, eventBus);
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
            {
                EventContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, data, switchHandler, EventHelper.findExecutor(DefaultEvent.SWITCH));
                eventBus.post(eventContext);
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return add(operator);
    }
    
    @Override
    public Pipeline from()
    {
        Operator operator = new Operator() {
            
            @SuppressWarnings("rawtypes")
            @Override
            public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
            {
                if (data instanceof int[])
                {
                    for (int i : (int[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof byte[])
                {
                    for (byte i : (byte[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof short[])
                {
                    for (short i : (short[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof long[])
                {
                    for (long i : (long[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof float[])
                {
                    for (float i : (float[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof double[])
                {
                    for (double i : (double[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof boolean[])
                {
                    for (boolean i : (boolean[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof char[])
                {
                    for (char i : (char[]) data)
                    {
                        pipeline.onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof Iterable)
                {
                    for (Object each : (Iterable) data)
                    {
                        pipeline.onCompleted(each, runnerMode);
                    }
                }
                else
                {
                    for (Object each : (Object[]) data)
                    {
                        pipeline.onCompleted(each, runnerMode);
                    }
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return add(operator);
    }
    
    public static Pipeline from(final Object data)
    {
        return new HeadPipeline() {
            @SuppressWarnings("rawtypes")
            @Override
            public void work(Object upstreamData, RunnerMode runnerMode)
            {
                if (data instanceof int[])
                {
                    for (int i : (int[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof byte[])
                {
                    for (byte i : (byte[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof short[])
                {
                    for (short i : (short[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof long[])
                {
                    for (long i : (long[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof float[])
                {
                    for (float i : (float[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof double[])
                {
                    for (double i : (double[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof boolean[])
                {
                    for (boolean i : (boolean[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof char[])
                {
                    for (char i : (char[]) data)
                    {
                        onCompleted(i, runnerMode);
                    }
                }
                else if (data instanceof Iterable)
                {
                    for (Object each : (Iterable) data)
                    {
                        onCompleted(each, runnerMode);
                    }
                }
                else
                {
                    for (Object each : (Object[]) data)
                    {
                        onCompleted(each, runnerMode);
                    }
                }
            };
        };
    }
    
    @Override
    public <E> Pipeline map(final MapOp<E> mapOp)
    {
        Operator operator = new Operator() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
            {
                pipeline.onCompleted(mapOp.map((E) data), runnerMode);
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return add(operator);
    }
    
    @Override
    public Pipeline distribute(final OperatorData... datas)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, Pipeline pipeline, RunnerMode runnerMode)
            {
                if (runnerMode.getEventBus() == null)
                {
                    runnerMode = new RunnerMode(ThreadMode.io, EventBuses.io());
                }
                for (OperatorData each : datas)
                {
                    Object eventData = data;
                    Object rowKey = data;
                    if (each.getEventData() != Pipeline.USE_UPSTREAM_RESULT)
                    {
                        eventData = each.getEventData();
                    }
                    if (each.getRowKey() != Pipeline.USE_UPSTREAM_RESULT)
                    {
                        rowKey = each.getRowKey();
                    }
                    EventContext<?> eventContext = proxy(pipeline, runnerMode, each.getEvent(), each.getHandler(), eventData, rowKey);
                    runnerMode.getEventBus().post(eventContext);
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return add(operator);
    }
    
}
