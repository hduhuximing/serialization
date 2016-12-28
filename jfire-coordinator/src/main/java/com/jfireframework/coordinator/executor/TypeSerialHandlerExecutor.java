package com.jfireframework.coordinator.executor;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.coordinator.handlercontext.CoordinatorContext;
import com.jfireframework.coordinator.util.CoordinatorHelper;
import com.jfireframework.coordinator.util.RunnerMode;

public class TypeSerialHandlerExecutor implements EventExecutor
{
    private static final int                 idle   = 0;
    private static final int                 busy   = 1;
    private AtomicInteger                    state  = new AtomicInteger(idle);
    private final MPSCQueue<CoordinatorContext<?>> events = new MPSCQueue<CoordinatorContext<?>>();
    
    @Override
    public void handle(CoordinatorContext<?> eventContext, RunnerMode runnerMode)
    {
        events.offer(eventContext);
        do
        {
            int current = state.get();
            if (current == idle && state.compareAndSet(current, busy))
            {
                while ((eventContext = events.poll()) != null)
                {
                    CoordinatorHelper.handle(eventContext, runnerMode);
                }
                state.set(idle);
                if (events.isEmpty())
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
