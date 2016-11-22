package com.jfireframework.eventbus.executor;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.util.EventHelper;

public class EventSerialHandlerExecutor implements EventExecutor
{
    private static final int                 idle   = 0;
    private static final int                 busy   = 1;
    private AtomicInteger                    state  = new AtomicInteger(idle);
    private final MPSCQueue<EventContext<?>> events = new MPSCQueue<EventContext<?>>();
    
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        events.offer(eventContext);
        do
        {
            int current = state.get();
            if (current == idle && state.compareAndSet(current, busy))
            {
                while ((eventContext = events.poll()) != null)
                {
                    EventHelper.handle(eventContext, eventBus);
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
        } while (true);
    }
}
