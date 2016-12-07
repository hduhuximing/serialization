package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.util.RunnerMode;

public interface InternalPipeline
{
    public static final Object USE_UPSTREAM_RESULT = new Object();
    
    /**
     * 开始本节点的逻辑任务。并且传入一个上游的结果参数
     * 
     * @param upstreamResult
     */
    public void work(Object upstreamResult, RunnerMode runnerMode);
    
    /**
     * 本节点逻辑任务完成后被调用，并且传入本节点的结果对象
     * 
     * @param result
     */
    public void onCompleted(Object result, RunnerMode runnerMode);
    
    /**
     * 本节点发生异常后调用
     * 
     * @param e
     */
    public void onError(Throwable e, RunnerMode runnerMode);
    
    /**
     * 管道开始投递
     */
    public void internalStart();
    
    /**
     * 管道开始投递，并且投递的时候携带一个初始参数
     * 
     * @param initParam
     */
    public void internalStart(Object initParam);
    
    /**
     * 增加一个pipeline到事件编排末尾
     * 
     * @param <T>
     * 
     * @param pipeline
     * @return
     */
    public InternalPipeline internalAdd(Operator operator);
    
}
