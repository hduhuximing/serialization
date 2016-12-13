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
import com.jfireframework.eventbus.operator.TransferOp;
import com.jfireframework.eventbus.operator.impl.FilterOp;
import com.jfireframework.eventbus.pipeline.InternalPipeline;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.pipeline.RowKeyFetcher;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;
import com.jfireframework.eventbus.util.RunnerMode.ThreadMode;
import com.jfireframework.eventbus.util.Schedules;
import com.jfireframework.eventbus.util.DefaultEvent;

public class DefaultPipeline extends BasePipeline
{
    
    public DefaultPipeline(Operator operator, InternalPipeline pred)
    {
        super(operator, pred);
    }
    
    public static Pipeline create()
    {
        return new HeadPipeline();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private EventContext<?> proxy(InternalPipeline pipeline, RunnerMode runnerMode, Enum<? extends EventConfig> event, EventHandler<?> handler, Object data, Object rowKey)
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
                if (rowKey == InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, data, handler, EventHelper.findExecutor(event));
                }
                else if (rowKey instanceof RowKeyFetcher)
                {
                    RowKeyFetcher fetcher = (RowKeyFetcher) rowKey;
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, fetcher.getRowKey(data), handler, EventHelper.findExecutor(event));
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
                if (rowKey == InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, data, handler, EventHelper.findExecutor(event));
                }
                else if (rowKey instanceof RowKeyFetcher)
                {
                    RowKeyFetcher fetcher = (RowKeyFetcher) rowKey;
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, fetcher.getRowKey(data), handler, EventHelper.findExecutor(event));
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
        private final InternalPipeline pipeline;
        
        public ReadWriteEventContextProxy(InternalPipeline pipeline, RunnerMode runnerMode, int mode, Object eventData, EventHandler<?> handler, EventExecutor executor)
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
        private final InternalPipeline pipeline;
        
        public RowEventContextProxy(InternalPipeline pipeline, RunnerMode runnerMode, Object eventData, Object rowkey, EventHandler<?> handler, EventExecutor executor)
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
        private final InternalPipeline pipeline;
        
        public NormalEventContextProxy(InternalPipeline pipeline, RunnerMode runnerMode, Object eventData, EventHandler<?> handler, EventExecutor executor)
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
    public Pipeline next(final Enum<? extends EventConfig> event, final EventHandler<?> handler, final Object eventData, final Object rowKey)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (eventData != InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    data = eventData;
                }
                EventContext<?> eventContext = proxy(pipeline, runnerMode, event, handler, data, rowKey);
                eventContext.run();
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline next(final Enum<? extends EventConfig> event, final EventHandler<?> handler, final Object eventData)
    {
        return next(event, handler, eventData, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    @Override
    public Pipeline next(final Enum<? extends EventConfig> event, final EventHandler<?> handler)
    {
        return next(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    @Override
    public Pipeline next(EventHandler<?> handler)
    {
        return next(DefaultEvent.JUST_PAEALLEL_EVENT, handler, USE_UPSTREAM_RESULT, USE_UPSTREAM_RESULT);
    }
    
    private static final EventHandler<Object> switchHandler = new EventHandler<Object>() {
        
        @Override
        public Object handle(Object data, RunnerMode runnerMode)
        {
            return data;
        }
    };
    
    @Override
    public Pipeline switchTo(final EventBus eventBus)
    {
        final RunnerMode newRunnerMode = new RunnerMode(ThreadMode.currentEventbus, eventBus);
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
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
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline from()
    {
        Operator operator = new Operator() {
            
            @SuppressWarnings("rawtypes")
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
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
        return (Pipeline) internalAdd(operator);
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
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                pipeline.onCompleted(mapOp.map((E) data), runnerMode);
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline distribute(final OperatorData... datas)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (runnerMode.getEventBus() == null)
                {
                    runnerMode = new RunnerMode(ThreadMode.io, EventBuses.io());
                }
                for (OperatorData each : datas)
                {
                    Object eventData = data;
                    Object rowKey = data;
                    if (each.getEventData() != InternalPipeline.USE_UPSTREAM_RESULT)
                    {
                        eventData = each.getEventData();
                    }
                    if (each.getRowKey() != InternalPipeline.USE_UPSTREAM_RESULT)
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
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline filter(final FilterOp filterOp)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (filterOp.prevent(data) == false)
                {
                    pipeline.onCompleted(data, runnerMode);
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline switchTo(Schedules schedules)
    {
        switch (schedules)
        {
            case IO:
                return switchTo(EventBuses.io());
            case COMPUTATION:
                return switchTo(EventBuses.computation());
            case NEW_THREAD:
                final RunnerMode newRunnerMode = new RunnerMode(ThreadMode.currentThread, null);
                Operator operator = new Operator() {
                    
                    @Override
                    public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
                    {
                        EventContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, data, switchHandler, EventHelper.findExecutor(DefaultEvent.SWITCH));
                        new Thread(eventContext).start();
                    }
                    
                    @Override
                    public void onError(Throwable e, RunnerMode runnerMode)
                    {
                        ;
                    }
                    
                };
                return (Pipeline) internalAdd(operator);
        }
        throw new NullPointerException();
    }
    
    @Override
    public Pipeline compose(TransferOp transferOp)
    {
        return transferOp.transfer(this);
    }
    
    @Override
    public Pipeline next(final Enum<? extends EventConfig> event, final EventHandler<?> handler, final Object eventData, final RowKeyFetcher rowKeyFetcher)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (eventData != InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    data = eventData;
                }
                EventContext<?> eventContext = proxy(pipeline, runnerMode, event, handler, data, rowKeyFetcher);
                eventContext.run();
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline next(Enum<? extends EventConfig> event, EventHandler<?> handler, RowKeyFetcher fetcher)
    {
        return next(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, fetcher);
    }
}
