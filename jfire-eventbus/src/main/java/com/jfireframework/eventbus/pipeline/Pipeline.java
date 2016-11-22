package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.pipeline.conversion.Conversion;

public interface Pipeline
{
    
    public static final Object USE_UPSTREAM_RESULT = new Object();
    
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowkey);
    
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData);
    
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public void signal();
    
    public static class PipelineData
    {
        final Object                      eventData;
        final Object                      rowKey;
        final Enum<? extends EventConfig> event;
        final EventHandler<?>             handler;
        
        public PipelineData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowKey)
        {
            this.eventData = eventData;
            this.rowKey = rowKey;
            this.event = event;
            this.handler = handler;
        }
        
        public PipelineData(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData)
        {
            this.eventData = eventData;
            rowKey = USE_UPSTREAM_RESULT;
            this.event = event;
            this.handler = handler;
        }
        
        public PipelineData(Enum<? extends EventConfig> event, EventHandler<?> handler)
        {
            eventData = USE_UPSTREAM_RESULT;
            rowKey = USE_UPSTREAM_RESULT;
            this.event = event;
            this.handler = handler;
        }
        
        public Object getEventData()
        {
            return eventData;
        }
        
        public Object getRowKey()
        {
            return rowKey;
        }
        
        public Enum<? extends EventConfig> getEvent()
        {
            return event;
        }
        
        public EventHandler<?> getHandler()
        {
            return handler;
        }
        
    }
    
    public Pipeline addAll(PipelineData... events);
    
    public Pipeline conversion(Conversion<?> conversion);
    
    /**
     * 开始本节点的逻辑任务。并且传入一个上游的结果参数
     * 
     * @param upstreamResult
     */
    public void work(Object upstreamResult);
    
    /**
     * 本节点逻辑任务完成后被调用，并且传入本节点的结果对象
     * 
     * @param result
     */
    public void onCompleted(Object result);
    
    public void setCompletedHanlder(CompletedHandler<?> completedHandler);
    
    /**
     * 管道开始投递
     */
    public void start();
    
    /**
     * 本节点发生异常后调用
     * 
     * @param e
     */
    public void onError(Throwable e);
    
    public Object getResult();
    
    public Throwable getThrowable();
    
    public void await();
    
}
