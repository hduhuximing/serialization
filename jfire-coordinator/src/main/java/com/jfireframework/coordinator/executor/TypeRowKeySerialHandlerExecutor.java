package com.jfireframework.coordinator.executor;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.coordinator.bus.CoordinatorBus;
import com.jfireframework.coordinator.bus.CoordinatorBuses;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.handlercontext.RowCoordinatorContext;
import com.jfireframework.coordinator.util.CoordinatorHelper;
import com.jfireframework.coordinator.util.RunnerMode;
import sun.misc.Unsafe;

public class TypeRowKeySerialHandlerExecutor implements EventExecutor
{
    private final ConcurrentHashMap<Object, RowBucket> map = new ConcurrentHashMap<Object, RowBucket>();
    
    static class RowBucket
    {
        public static final int                     IN_WORK        = 1;
        public static final int                     END_OF_WORK    = -1;
        public static final int                     SENDING_LEFT   = -2;
        public static final int                     END_OF_SENDING = -3;
        private volatile int                        status         = IN_WORK;
        private final MPSCQueue<RowCoordinatorContext<?>> eventQueue     = new MPSCQueue<RowCoordinatorContext<?>>();
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
    public void handle(CoordinatorContext<?> eventContext, RunnerMode runnerMode)
    {
        RowCoordinatorContext<?> rowEventContext = (RowCoordinatorContext<?>) eventContext;
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
                    CoordinatorHelper.handle(rowEventContext, runnerMode);
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
        CoordinatorContext<?> rowEventContext;
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
                CoordinatorBus eventBus;
                if (runnerMode.getEventBus() != null)
                {
                    eventBus = runnerMode.getEventBus();
                }
                else
                {
                    eventBus = CoordinatorBuses.computation();
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
