package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.baseutil.concurrent.SingleSync;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.completedhandler.impl.CallNextPipeline;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.eventcontext.impl.NormalEventContext;
import com.jfireframework.eventbus.eventcontext.impl.ReadWriteEventContextImpl;
import com.jfireframework.eventbus.eventcontext.impl.RowEventContextImpl;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.Operator;
import com.jfireframework.eventbus.pipeline.Pipeline;

public abstract class AbstractPipeline implements Pipeline
{
    protected final ParallelLevel      level;
    protected final EventExecutor      executor;
    protected final EventBus           eventBus;
    protected SingleSync               sync = new SingleSync();
    protected Throwable                e;
    protected CompletedHandler<Object> pipelineCompletedHandler;
    protected final Object             eventData;
    protected final EventHandler<?>    eventHandler;
    protected final Object             rowKey;
    protected Object                   result;
    
    public AbstractPipeline(EventBus eventBus, EventExecutor executor, EventHandler<?> handler, ParallelLevel level, Object eventData, Object rowKey)
    {
        this.eventBus = eventBus;
        this.executor = executor;
        this.eventHandler = handler;
        this.level = level;
        this.eventData = eventData;
        this.rowKey = rowKey;
    }
    
    @Override
    public Pipeline addOperator(final Operator operator)
    {
        Pipeline pipeline = new AbstractPipeline(eventBus, operator.executor(), operator.handler(), operator.level(), operator.eventData(), operator.rowKey()) {
            
            @Override
            public void work(Object upstreamResult)
            {
                operator.work(build(upstreamResult), eventBus);
            }
            
            @Override
            public void start(Object initParam)
            {
                this.sync.reset();
                AbstractPipeline.this.start(initParam);
            }
            
            @Override
            public void start()
            {
                this.sync.reset();
                AbstractPipeline.this.start();
            }
            
        };
        pipelineCompletedHandler = new CallNextPipeline(pipeline);
        return pipeline;
    }
    
    @Override
    public void onCompleted(Object result)
    {
        this.result = result;
        sync.signal();
        if (pipelineCompletedHandler != null)
        {
            pipelineCompletedHandler.onCompleted(result);
        }
    }
    
    @Override
    public void await()
    {
        sync.await();
    }
    
    @Override
    public void setCompletedHanlder(CompletedHandler<Object> completedHandler)
    {
        this.pipelineCompletedHandler = completedHandler;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected EventContext<?> build(Object upStreamResult)
    {
        if (this.eventData == USE_UPSTREAM_RESULT)
        {
            switch (level)
            {
                case PAEALLEL:
                    return new NormalEventContextProxy(upStreamResult, eventHandler, executor, eventBus);
                case EVENT_SERIAL:
                    return new NormalEventContextProxy(upStreamResult, eventHandler, executor, eventBus);
                case ROWKEY_SERIAL:
                    if (rowKey == USE_UPSTREAM_RESULT)
                    {
                        return new RowEventContextProxy(upStreamResult, eventHandler, executor, eventBus, upStreamResult);
                    }
                    else
                    {
                        return new RowEventContextProxy(upStreamResult, eventHandler, executor, eventBus, rowKey);
                    }
                case RW_EVENT_READ:
                    return new ReadWriteEventContextProxy(ReadWriteEventContext.READ, upStreamResult, eventHandler, executor, eventBus);
                case RW_EVENT_WRITE:
                    return new ReadWriteEventContextProxy(ReadWriteEventContext.WRITE, upStreamResult, eventHandler, executor, eventBus);
                case TYPE_ROWKEY_SERIAL:
                    if (rowKey == USE_UPSTREAM_RESULT)
                    {
                        return new RowEventContextProxy(upStreamResult, eventHandler, executor, eventBus, upStreamResult);
                    }
                    else
                    {
                        return new RowEventContextProxy(upStreamResult, eventHandler, executor, eventBus, rowKey);
                    }
                case TYPE_SERIAL:
                    return new NormalEventContextProxy(upStreamResult, eventHandler, executor, eventBus);
                default:
                    throw new NullPointerException();
            }
        }
        else
        {
            switch (level)
            {
                case PAEALLEL:
                    return new NormalEventContextProxy(eventData, eventHandler, executor, eventBus);
                case EVENT_SERIAL:
                    return new NormalEventContextProxy(eventData, eventHandler, executor, eventBus);
                case ROWKEY_SERIAL:
                    if (rowKey == USE_UPSTREAM_RESULT)
                    {
                        return new RowEventContextProxy(eventData, eventHandler, executor, eventBus, upStreamResult);
                    }
                    else
                    {
                        return new RowEventContextProxy(eventData, eventHandler, executor, eventBus, rowKey);
                    }
                case RW_EVENT_READ:
                    return new ReadWriteEventContextProxy(ReadWriteEventContext.READ, eventData, eventHandler, executor, eventBus);
                case RW_EVENT_WRITE:
                    return new ReadWriteEventContextProxy(ReadWriteEventContext.WRITE, eventData, eventHandler, executor, eventBus);
                case TYPE_ROWKEY_SERIAL:
                    if (rowKey == USE_UPSTREAM_RESULT)
                    {
                        return new RowEventContextProxy(eventData, eventHandler, executor, eventBus, upStreamResult);
                    }
                    else
                    {
                        return new RowEventContextProxy(eventData, eventHandler, executor, eventBus, rowKey);
                    }
                case TYPE_SERIAL:
                    return new NormalEventContextProxy(eventData, eventHandler, executor, eventBus);
                default:
                    throw new NullPointerException();
            }
        }
    }
    
    @Override
    public void onError(Throwable e)
    {
        this.e = e;
        sync.signal();
        if (pipelineCompletedHandler != null)
        {
            pipelineCompletedHandler.onError(e);
        }
    }
    
    @Override
    public Object getResult()
    {
        return result;
    }
    
    @Override
    public Throwable getThrowable()
    {
        return e;
    }
    
    @Override
    public boolean hasError()
    {
        return e != null;
    }
    
    class ReadWriteEventContextProxy<T> extends ReadWriteEventContextImpl<T>
    {
        
        public ReadWriteEventContextProxy(int mode, Object eventData, EventHandler<?> handler, EventExecutor executor, EventBus eventBus)
        {
            super(mode, eventData, handler, executor, eventBus);
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            onCompleted(result);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            onError(e);
        }
    }
    
    class RowEventContextProxy<T> extends RowEventContextImpl<T>
    {
        
        public RowEventContextProxy(Object eventData, EventHandler<?> handler, EventExecutor executor, EventBus eventBus, Object rowkey)
        {
            super(eventData, handler, executor, eventBus, rowkey);
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            onCompleted(result);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            onError(e);
        }
    }
    
    class NormalEventContextProxy<T> extends NormalEventContext<T>
    {
        
        public NormalEventContextProxy(Object eventData, EventHandler<?> handler, EventExecutor executor, EventBus eventBus)
        {
            super(eventData, handler, executor, eventBus);
        }
        
        @Override
        public void setResult(Object result)
        {
            super.setResult(result);
            onCompleted(result);
        }
        
        @Override
        public void setThrowable(Throwable e)
        {
            super.setThrowable(e);
            onError(e);
        }
    }
    
}
