package com.jfireframework.eventbus.pipeline;

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

public class PipeLineImpl implements PipeLine
{
    
    class Node
    {
        EventContext<?> eventContext;
        Node            next;
        
        public Node(EventContext<?> eventContext)
        {
            this.eventContext = eventContext;
        }
        
    }
    
    protected final IdentityHashMap<Enum<? extends EventConfig>, HandlerCombination>   combinationMap;
    protected final IdentityHashMap<Enum<? extends EventConfig>, EventHandlerExecutor> executorMap;
    protected static final Logger                                                      LOGGER   = ConsoleLogFactory.getLogger();
    protected EventBus                                                                 eventBus;
    private Node                                                                       head;
    private Node                                                                       tail;
    protected volatile boolean                                                         finished = false;
    protected Thread                                                                   owner;
    protected volatile boolean                                                         await    = false;
    protected Throwable                                                                e;
    private volatile boolean                                                           started  = false;
    
    public PipeLineImpl(IdentityHashMap<Enum<? extends EventConfig>, HandlerCombination> combinationMap, IdentityHashMap<Enum<? extends EventConfig>, EventHandlerExecutor> executorMap, EventBus eventBus)
    {
        this.combinationMap = combinationMap;
        this.executorMap = executorMap;
        this.eventBus = eventBus;
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void add(Object data, Enum<? extends EventConfig> event, Object rowkey)
    {
        if (((EventConfig) event).parallelLevel() != ParallelLevel.ROWKEY_SERIAL && ((EventConfig) event).parallelLevel() != ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法只能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
        EventContext<?> eventContext = new RowEventContextImpl(data, event, combinationMap.get(event).combination(), executorMap.get(event), eventBus, rowkey);
        addEventContext(eventContext);
    }
    
    private void addEventContext(EventContext<?> eventContext)
    {
        Node node = new Node(eventContext);
        if (tail == null)
        {
            tail = node;
            head = node;
        }
        else
        {
            tail.next = node;
            tail = node;
        }
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void add(Object data, Enum<? extends EventConfig> event)
    {
        if (((EventConfig) event).parallelLevel() == ParallelLevel.ROWKEY_SERIAL || ((EventConfig) event).parallelLevel() == ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法不能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
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
        addEventContext(eventContext);
    }
    
    @Override
    public EventHandlerExecutor executor()
    {
        return head.eventContext.executor();
    }
    
    @Override
    public EventHandler<?, ?>[] combinationHandlers()
    {
        return head.eventContext.combinationHandlers();
    }
    
    @Override
    public void setResult(Object trans)
    {
        head.eventContext.setResult(trans);
    }
    
    @Override
    public Object getResult()
    {
        throw new UnsupportedOperationException("管道操作，不支持返回结果，因为结果可能会多余一个");
    }
    
    @Override
    public Object getResult(long mills) throws InterruptedException
    {
        throw new UnsupportedOperationException("管道操作，不支持返回结果，因为结果可能会多余一个");
    }
    
    @Override
    public Object getEventData()
    {
        return head.eventContext.getEventData();
    }
    
    @Override
    public Enum<? extends EventConfig> getEvent()
    {
        return head.eventContext.getEvent();
    }
    
    @Override
    public void await()
    {
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
        head.eventContext.setThrowable(e);
        this.e = e;
    }
    
    @Override
    public Throwable getThrowable()
    {
        return e;
    }
    
    @Override
    public void signal()
    {
        head.eventContext.signal();
        if (e == null)
        {
            head = head.next;
            if (head != null)
            {
                eventBus.post(this);
                return;
            }
            else
            {
                ;
            }
        }
        else
        {
            ;
        }
        finished = true;
        if (await)
        {
            LockSupport.unpark(owner);
        }
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
            eventBus.post(this);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
}
