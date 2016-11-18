package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
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
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.handler.HandlerCombination;
import com.jfireframework.eventbus.pipeline.Pipeline;
import com.jfireframework.eventbus.util.EventHelper;

public class PipelineImpl implements Pipeline
{
    private static final Object DELIVERY = new Object();
    
    class Node
    {
        private final Object                      data;
        private final Enum<? extends EventConfig> event;
        private final Object                      rowKey;
        private Node                              next;
        
        public Node(Object data, Enum<? extends EventConfig> event, Object rowKey)
        {
            this.data = data;
            this.event = event;
            this.rowKey = rowKey;
        }
        
        public Node(Object data, Enum<? extends EventConfig> event)
        {
            this.data = data;
            this.event = event;
            rowKey = null;
        }
        
        public Node(Enum<? extends EventConfig> event)
        {
            this.event = event;
            rowKey = null;
            data = DELIVERY;
        }
        
        public Node(Enum<? extends EventConfig> event, Object rowKey)
        {
            this.event = event;
            this.rowKey = rowKey;
            data = DELIVERY;
        }
    }
    
    protected static final Logger                                                      LOGGER   = ConsoleLogFactory.getLogger();
    protected final IdentityHashMap<Enum<? extends EventConfig>, HandlerCombination>   combinationMap;
    protected final IdentityHashMap<Enum<? extends EventConfig>, EventHandlerExecutor> executorMap;
    protected final EventBus                                                           eventBus;
    protected volatile boolean                                                         finished = false;
    protected Thread                                                                   owner;
    protected volatile boolean                                                         await    = false;
    protected Throwable                                                                e;
    private volatile boolean                                                           started  = false;
    private Object                                                                     result;
    private Node                                                                       head;
    private Node                                                                       tail;
    private EventContext<?>                                                            now;
    
    public PipelineImpl(IdentityHashMap<Enum<? extends EventConfig>, HandlerCombination> combinationMap, IdentityHashMap<Enum<? extends EventConfig>, EventHandlerExecutor> executorMap, EventBus eventBus)
    {
        this.combinationMap = combinationMap;
        this.executorMap = executorMap;
        this.eventBus = eventBus;
    }
    
    @Override
    public void await()
    {
        if (started == false)
        {
            throw new UnsupportedOperationException("还未开始投递");
        }
        owner = Thread.currentThread();
        await = true;
        while (finished == false)
        {
            LockSupport.park();
        }
    }
    
    @Override
    public void setThrowable(Throwable e)
    {
        this.e = e;
        now.setThrowable(e);
    }
    
    @Override
    public Throwable getThrowable()
    {
        return e;
    }
    
    @Override
    public void await(long mills)
    {
        owner = Thread.currentThread();
        await = true;
        long left = TimeUnit.MILLISECONDS.toNanos(mills);
        while (finished == false)
        {
            long t0 = System.nanoTime();
            LockSupport.parkNanos(left);
            long t1 = System.nanoTime();
            left -= t1 - t0;
            if (left < 1000)
            {
                // 1000纳秒其实非常短，使用循环等待就好了
                for (int i = 0; i < 10000; i++)
                {
                    ;
                }
                break;
            }
        }
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public void start()
    {
        if (started == false)
        {
            started = true;
            now = next();
            if (now == null)
            {
                throw new NullPointerException();
            }
            eventBus.post(this);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    private EventContext<?> next()
    {
        if (head != null)
        {
            EventContext<?> eventContext;
            if (head.data == DELIVERY)
            {
                if (head.rowKey == null)
                {
                    eventContext = build(result, head.event);
                }
                else
                {
                    eventContext = build(result, head.event, head.rowKey);
                }
            }
            else
            {
                if (head.rowKey == null)
                {
                    eventContext = build(head.data, head.event);
                }
                else
                {
                    eventContext = build(head.data, head.event, head.rowKey);
                }
            }
            head = head.next;
            return eventContext;
        }
        else
        {
            return null;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected EventContext<?> build(Object data, Enum<? extends EventConfig> event, Object rowkey)
    {
        EventHelper.checkParallelLevel(event, rowkey);
        return new RowEventContextImpl(data, event, combinationMap.get(event).combination(), executorMap.get(event), eventBus, rowkey);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected EventContext<?> build(Object data, Enum<? extends EventConfig> event)
    {
        EventHelper.checkParallelLevel(event);
        EventConfig config = (EventConfig) event;
        EventContext<?> eventContext;
        if (config.parallelLevel() == ParallelLevel.RW_EVENT_READ)
        {
            eventContext = new ReadWriteEventContextImpl(ReadWriteEventContext.READ, data, event, combinationMap.get(event).combination(), executorMap.get(event), eventBus);
        }
        else if (config.parallelLevel() == ParallelLevel.RW_EVENT_WRITE)
        {
            eventContext = new ReadWriteEventContextImpl(ReadWriteEventContext.WRITE, data, event, combinationMap.get(event).combination(), executorMap.get(event), eventBus);
        }
        else
        {
            eventContext = new NormalEventContext(data, event, combinationMap.get(event).combination(), executorMap.get(event), eventBus);
        }
        return eventContext;
    }
    
    @Override
    public EventHandlerExecutor executor()
    {
        return now.executor();
    }
    
    @Override
    public EventHandler<?, ?>[] combinationHandlers()
    {
        return now.combinationHandlers();
    }
    
    @Override
    public void setResult(Object result)
    {
        this.result = result;
        now.setResult(result);
    }
    
    @Override
    public Object getResult()
    {
        return result;
    }
    
    @Override
    public Object getResult(long mills) throws InterruptedException
    {
        if (finished)
        {
            return result;
        }
        await(mills);
        if (finished)
        {
            return result;
        }
        else
        {
            throw new InterruptedException();
        }
    }
    
    @Override
    public void signal()
    {
        now.signal();
        if (e == null && (now = next()) != null)
        {
            eventBus.post(this);
        }
        else
        {
            finished = true;
            if (await)
            {
                LockSupport.unpark(owner);
            }
        }
    }
    
    @Override
    public Object getEventData()
    {
        return now.getEventData();
    }
    
    @Override
    public Enum<? extends EventConfig> getEvent()
    {
        return now.getEvent();
    }
    
    @Override
    public void add(Object data, Enum<? extends EventConfig> event, Object rowkey)
    {
        EventHelper.checkParallelLevel(event, rowkey);
        addNode(new Node(data, event, rowkey));
    }
    
    private void addNode(Node node)
    {
        if (head == null)
        {
            head = node;
            tail = node;
        }
        else
        {
            tail.next = node;
            tail = node;
        }
    }
    
    @Override
    public void add(Object data, Enum<? extends EventConfig> event)
    {
        EventHelper.checkParallelLevel(event);
        addNode(new Node(data, event));
    }
    
    @Override
    public void add(Enum<? extends EventConfig> event, Object rowkey)
    {
        EventHelper.checkParallelLevel(event, rowkey);
        addNode(new Node(event, rowkey));
    }
    
    @Override
    public void add(Enum<? extends EventConfig> event)
    {
        EventHelper.checkParallelLevel(event);
        addNode(new Node(event));
    }
}
