package com.jfireframework.eventbus.pipeline.impl;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.pipeline.Pipeline;

public class DistributivePipeline extends AbstractPipeline
{
    private List<Pipeline> pipelines = new LinkedList<Pipeline>();
    
    public DistributivePipeline(EventBus eventBus, IdentityHashMap<Enum<? extends EventConfig>, EventExecutor> executorMap, Pipeline pre, PipelineData... eventWithDatas)
    {
        super(eventBus, executorMap, pre, null, null, null);
        for (PipelineData data : eventWithDatas)
        {
            Pipeline pipeline = add(data.getEvent(), data.getHandler(), data.getEventData(), data.getRowKey());
            pipelines.add(pipeline);
            pipeline.setCompletedHanlder(
                    new CompletedHandler<Object>() {
                        
                        @Override
                        public void onCompleted(Object result)
                        {
                            DistributivePipeline.this.onCompleted(result);
                        }
                        
                        @Override
                        public void onError(Throwable e)
                        {
                            DistributivePipeline.this.onError(e);
                        }
                    }
            );
        }
        pipelineCompletedHandler = null;
    }
    
    @Override
    public void work(Object upstreamResult)
    {
        for (Pipeline each : pipelines)
        {
            each.work(upstreamResult);
        }
    }
    
}
