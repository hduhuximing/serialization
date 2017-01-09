package com.jfireframework.coordinator.pipeline.impl;

import com.jfireframework.coordinator.api.CoordinatorHandler;
import com.jfireframework.coordinator.api.CoordinatorConfig;
import com.jfireframework.coordinator.bus.CoordinatorBus;
import com.jfireframework.coordinator.bus.CoordinatorBuses;
import com.jfireframework.coordinator.executor.EventExecutor;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.handlercontext.ReadWriteCoordinatorContext;
import com.jfireframework.coordinator.handlercontext.impl.NormalCoordinatorContext;
import com.jfireframework.coordinator.handlercontext.impl.ReadWriteCoordinatorContextImpl;
import com.jfireframework.coordinator.handlercontext.impl.RowCoordinatorContextImpl;
import com.jfireframework.coordinator.operator.FilterOp;
import com.jfireframework.coordinator.operator.MapOp;
import com.jfireframework.coordinator.operator.Operator;
import com.jfireframework.coordinator.operator.OperatorData;
import com.jfireframework.coordinator.operator.TransferOp;
import com.jfireframework.coordinator.pipeline.InternalPipeline;
import com.jfireframework.coordinator.pipeline.Pipeline;
import com.jfireframework.coordinator.pipeline.RowKeyFetcher;
import com.jfireframework.coordinator.util.DefaultCoordinator;
import com.jfireframework.coordinator.util.CoordinatorHelper;
import com.jfireframework.coordinator.util.RunnerMode;
import com.jfireframework.coordinator.util.Schedules;
import com.jfireframework.coordinator.util.RunnerMode.ThreadMode;

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
    private CoordinatorContext<?> proxy(InternalPipeline pipeline, RunnerMode runnerMode, Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, Object data, Object rowKey)
    {
        CoordinatorContext<?> eventContext;
        switch (((CoordinatorConfig) event).parallelLevel())
        {
            case PAEALLEL:
                eventContext = new NormalEventContextProxy(pipeline, runnerMode, data, handler, CoordinatorHelper.findExecutor(event));
                break;
            case EVENT_SERIAL:
                eventContext = new NormalEventContextProxy(pipeline, runnerMode, data, handler, CoordinatorHelper.findExecutor(event));
                break;
            case ROWKEY_SERIAL:
                if (rowKey == InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, data, handler, CoordinatorHelper.findExecutor(event));
                }
                else if (rowKey instanceof RowKeyFetcher)
                {
                    RowKeyFetcher fetcher = (RowKeyFetcher) rowKey;
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, fetcher.getRowKey(data), handler, CoordinatorHelper.findExecutor(event));
                }
                else
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, rowKey, handler, CoordinatorHelper.findExecutor(event));
                }
                break;
            case RW_EVENT_READ:
                eventContext = new ReadWriteEventContextProxy(pipeline, runnerMode, ReadWriteCoordinatorContext.READ, data, handler, CoordinatorHelper.findExecutor(event));
                break;
            case RW_EVENT_WRITE:
                eventContext = new ReadWriteEventContextProxy(pipeline, runnerMode, ReadWriteCoordinatorContext.WRITE, data, handler, CoordinatorHelper.findExecutor(event));
                break;
            case TYPE_ROWKEY_SERIAL:
                if (rowKey == InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, data, handler, CoordinatorHelper.findExecutor(event));
                }
                else if (rowKey instanceof RowKeyFetcher)
                {
                    RowKeyFetcher fetcher = (RowKeyFetcher) rowKey;
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, fetcher.getRowKey(data), handler, CoordinatorHelper.findExecutor(event));
                }
                else
                {
                    eventContext = new RowEventContextProxy(pipeline, runnerMode, data, rowKey, handler, CoordinatorHelper.findExecutor(event));
                }
                break;
            case TYPE_SERIAL:
                eventContext = new NormalEventContextProxy(pipeline, runnerMode, data, handler, CoordinatorHelper.findExecutor(event));
                break;
            default:
                throw new NullPointerException();
        }
        return eventContext;
    }
    
    static class ReadWriteEventContextProxy<T> extends ReadWriteCoordinatorContextImpl<T>
    {
        private final InternalPipeline pipeline;
        
        public ReadWriteEventContextProxy(InternalPipeline pipeline, RunnerMode runnerMode, int mode, Object eventData, CoordinatorHandler<?> handler, EventExecutor executor)
        {
            super(runnerMode, mode, eventData, handler, executor);
            this.pipeline = pipeline;
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            pipeline.onNext(result, runnerMode);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            pipeline.onError(e, runnerMode);
        }
    }
    
    static class RowEventContextProxy<T> extends RowCoordinatorContextImpl<T>
    {
        private final InternalPipeline pipeline;
        
        public RowEventContextProxy(InternalPipeline pipeline, RunnerMode runnerMode, Object eventData, Object rowkey, CoordinatorHandler<?> handler, EventExecutor executor)
        {
            super(runnerMode, eventData, handler, executor, rowkey);
            this.pipeline = pipeline;
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            pipeline.onNext(result, runnerMode);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            pipeline.onError(e, runnerMode);
        }
    }
    
    static class NormalEventContextProxy<T> extends NormalCoordinatorContext<T>
    {
        private final InternalPipeline pipeline;
        
        public NormalEventContextProxy(InternalPipeline pipeline, RunnerMode runnerMode, Object eventData, CoordinatorHandler<?> handler, EventExecutor executor)
        {
            super(runnerMode, eventData, handler, executor);
            this.pipeline = pipeline;
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            pipeline.onNext(result, runnerMode);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            pipeline.onError(e, runnerMode);
        }
    }
    
    @Override
    public Pipeline next(final Enum<? extends CoordinatorConfig> event, final CoordinatorHandler<?> handler, final Object eventData, final Object rowKey)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (eventData != InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    data = eventData;
                }
                CoordinatorContext<?> eventContext = proxy(pipeline, runnerMode, event, handler, data, rowKey);
                eventContext.run();
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
            {
                ;
            }
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline next(final Enum<? extends CoordinatorConfig> event, final CoordinatorHandler<?> handler, final Object eventData)
    {
        return next(event, handler, eventData, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    @Override
    public Pipeline next(final Enum<? extends CoordinatorConfig> event, final CoordinatorHandler<?> handler)
    {
        return next(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, InternalPipeline.USE_UPSTREAM_RESULT);
    }
    
    @Override
    public Pipeline next(CoordinatorHandler<?> handler)
    {
        return next(DefaultCoordinator.JUST_PAEALLEL_EVENT, handler, USE_UPSTREAM_RESULT, USE_UPSTREAM_RESULT);
    }
    
    private static final CoordinatorHandler<Object> switchHandler = new CoordinatorHandler<Object>() {
        
        @Override
        public Object handle(Object data, RunnerMode runnerMode)
        {
            return data;
        }
    };
    
    @Override
    public Pipeline switchTo(final CoordinatorBus eventBus)
    {
        final RunnerMode newRunnerMode = new RunnerMode(ThreadMode.currentEventbus, eventBus);
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, data, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                eventBus.post(eventContext);
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline from()
    {
        final RunnerMode newRunnerMode = new RunnerMode(ThreadMode.currentEventbus, CoordinatorBuses.io());
        Operator operator = new Operator() {
            
            @SuppressWarnings("rawtypes")
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (data instanceof int[])
                {
                    for (int i : (int[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof byte[])
                {
                    for (byte i : (byte[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof short[])
                {
                    for (short i : (short[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof long[])
                {
                    for (long i : (long[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof float[])
                {
                    for (float i : (float[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof double[])
                {
                    for (double i : (double[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof boolean[])
                {
                    for (boolean i : (boolean[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof char[])
                {
                    for (char i : (char[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, i, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else if (data instanceof Iterable)
                {
                    for (Object each : (Iterable) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, each, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
                else
                {
                    for (Object each : (Object[]) data)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, each, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        CoordinatorBuses.io().post(eventContext);
                    }
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
            {
                ;
            }
            
        };
        return (Pipeline) internalAdd(operator);
    }
    
    public static Pipeline from(final Object data)
    {
        return new HeadPipeline() {
            @Override
            public void internalStart()
            {
                work(data, new RunnerMode(ThreadMode.currentThread, null));
            }
            
            @Override
            public void internalStart(Object initParam)
            {
                work(data, new RunnerMode(ThreadMode.currentThread, null));
            }
        }.from();
    }
    
    @Override
    public <E> Pipeline map(final MapOp<E> mapOp)
    {
        Operator operator = new Operator() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                pipeline.onNext(mapOp.map((E) data), runnerMode);
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
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
                    runnerMode = new RunnerMode(ThreadMode.io, CoordinatorBuses.io());
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
                    CoordinatorContext<?> eventContext = proxy(pipeline, runnerMode, each.getEvent(), each.getHandler(), eventData, rowKey);
                    runnerMode.getEventBus().post(eventContext);
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
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
                    pipeline.onNext(data, runnerMode);
                }
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
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
                return switchTo(CoordinatorBuses.io());
            case COMPUTATION:
                return switchTo(CoordinatorBuses.computation());
            case NEW_THREAD:
                final RunnerMode newRunnerMode = new RunnerMode(ThreadMode.currentThread, null);
                Operator operator = new Operator() {
                    
                    @Override
                    public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
                    {
                        CoordinatorContext<?> eventContext = new NormalEventContextProxy<Object>(pipeline, newRunnerMode, data, switchHandler, CoordinatorHelper.findExecutor(DefaultCoordinator.SWITCH));
                        new Thread(eventContext).start();
                    }
                    
                    @Override
                    public void onError(Throwable e, RunnerMode runnerMode)
                    {
                        ;
                    }
                    
                    @Override
                    public void onComplete(Object result, RunnerMode runnerMode)
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
    public Pipeline next(final Enum<? extends CoordinatorConfig> event, final CoordinatorHandler<?> handler, final Object eventData, final RowKeyFetcher<?> rowKeyFetcher)
    {
        Operator operator = new Operator() {
            
            @Override
            public void work(Object data, InternalPipeline pipeline, RunnerMode runnerMode)
            {
                if (eventData != InternalPipeline.USE_UPSTREAM_RESULT)
                {
                    data = eventData;
                }
                CoordinatorContext<?> eventContext = proxy(pipeline, runnerMode, event, handler, data, rowKeyFetcher);
                eventContext.run();
            }
            
            @Override
            public void onError(Throwable e, RunnerMode runnerMode)
            {
                ;
            }
            
            @Override
            public void onComplete(Object result, RunnerMode runnerMode)
            {
                ;
            }
        };
        return (Pipeline) internalAdd(operator);
    }
    
    @Override
    public Pipeline next(Enum<? extends CoordinatorConfig> event, CoordinatorHandler<?> handler, RowKeyFetcher<?> fetcher)
    {
        return next(event, handler, InternalPipeline.USE_UPSTREAM_RESULT, fetcher);
    }
}
