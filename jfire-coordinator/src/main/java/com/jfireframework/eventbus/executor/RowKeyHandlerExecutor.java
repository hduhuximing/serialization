package com.jfireframework.eventbus.executor;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.EventBuses;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.RowEventContext;
import com.jfireframework.eventbus.util.EventHelper;
import com.jfireframework.eventbus.util.RunnerMode;
import sun.misc.Unsafe;

public class RowKeyHandlerExecutor implements EventExecutor
{
    private final ConcurrentHashMap<Object, RowBucket> map = new ConcurrentHashMap<Object, RowBucket>();
    
    static class RowBucket
    {
        public static final int                     IN_WORK        = 1;
        public static final int                     END_OF_WORK    = -1;
        public static final int                     SENDING_LEFT   = -2;
        public static final int                     END_OF_SENDING = -3;
        private volatile int                        status         = IN_WORK;
        private final MPSCQueue<RowEventContext<?>> eventQueue     = new MPSCQueue<RowEventContext<?>>();
        private static final long                   offset         = ReflectUtil.getFieldOffset("status", RowBucket.class);
        private static final Unsafe                 unsafe         = ReflectUtil.getUnsafe();
        
        public boolean takeControlOfSendingLeft()
        {
            int now = status;
            if (now == END_OF_WORK || now == END_OF_SENDING)
            {
                return unsafe.compareAndSwapInt(this, offset, now, SENDING_LEFT);
            }
            else
            {
                return false;
            }
        }
    }
    
    @Override
    public void handle(EventContext<?> eventContext, RunnerMode runnerMode)
    {
        RowEventContext<?> rowEventContext = (RowEventContext<?>) eventContext;
        Object rowKey = rowEventContext.rowkey();
        RowBucket rowBucket = map.get(rowKey);
        if (rowBucket != null)
        {
            rowBucket.eventQueue.offer(rowEventContext);
            trySendLeft(rowBucket, runnerMode);
        }
        else
        {
            rowBucket = new RowBucket();
            RowBucket pre = map.putIfAbsent(rowKey, rowBucket);
            if (pre == null)
            {
                rowBucket.eventQueue.offer(rowEventContext);
                while ((rowEventContext = rowBucket.eventQueue.poll()) != null)
                {
                    EventHelper.handle(rowEventContext, runnerMode);
                }
                map.remove(rowKey);
                rowBucket.status = RowBucket.END_OF_WORK;
                trySendLeft(rowBucket, runnerMode);
            }
            else
            {
                pre.eventQueue.offer(rowEventContext);
                trySendLeft(pre, runnerMode);
            }
        }
    }
    
    private void trySendLeft(RowBucket rowBucket, RunnerMode runnerMode)
    {
        EventContext<?> rowEventContext;
        int status = rowBucket.status;
        if (status == RowBucket.IN_WORK || status == RowBucket.SENDING_LEFT)
        {
            return;
        }
        do
        {
            status = rowBucket.status;
            if ((status == RowBucket.END_OF_WORK && rowBucket.takeControlOfSendingLeft()) //
                    || (status == RowBucket.END_OF_SENDING && rowBucket.eventQueue.isEmpty() == false && rowBucket.takeControlOfSendingLeft()))
            {
                EventBus eventBus;
                if (runnerMode.getEventBus() != null)
                {
                    eventBus = runnerMode.getEventBus();
                }
                else
                {
                    eventBus = EventBuses.computation();
                }
                while ((rowEventContext = rowBucket.eventQueue.poll()) != null)
                {
                    eventBus.post(rowEventContext);
                }
                rowBucket.status = RowBucket.END_OF_SENDING;
                if (rowBucket.eventQueue.isEmpty())
                {
                    break;
                }
                else
                {
                    continue;
                }
            }
            else
            {
                break;
            }
        } while (true);
    }
    
}
