package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.handler.EventHandler;

public interface Pipeline
{
    public Pipeline add(Object eventData, Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public Pipeline add(Object eventData, Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public Pipeline add(Enum<? extends EventConfig> event, Object rowkey, EventHandler<?> handler);
    
    public Pipeline add(Enum<? extends EventConfig> event, EventHandler<?> handler);
    
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
    
    public void completedHanlder(CompletedHandler<?> completedHandler);
    
    /**
     * 管道开始投递
     */
    public void start();
    
    public void onError(Throwable e);
    
    public Object getResult();
    
    public Throwable getThrowable();
    
    public void await();
    
}
