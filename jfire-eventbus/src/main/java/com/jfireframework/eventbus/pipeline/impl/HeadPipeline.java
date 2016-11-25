package com.jfireframework.eventbus.pipeline.impl;

import com.jfireframework.eventbus.util.RunnerMode;
import com.jfireframework.eventbus.util.RunnerMode.ThreadMode;

public class HeadPipeline extends DefaultPipeline
{
    
    public HeadPipeline()
    {
        super(null, null);
    }
    
    /**
     * 开始本节点的逻辑任务。并且传入一个上游的结果参数
     * 
     * @param upstreamResult
     */
    @Override
    public void work(Object upstreamResult, RunnerMode runnerMode)
    {
        onCompleted(upstreamResult, runnerMode);
    }
    
    /**
     * 本节点逻辑任务完成后被调用，并且传入本节点的结果对象
     * 
     * @param result
     */
    @Override
    public void onCompleted(Object result, RunnerMode runnerMode)
    {
        if (next != null)
        {
            next.work(result, runnerMode);
        }
    }
    
    /**
     * 本节点发生异常后调用
     * 
     * @param e
     */
    @Override
    public void onError(Throwable e, RunnerMode runnerMode)
    {
        if (next != null)
        {
            next.onError(e, runnerMode);
        }
    }
    
    /**
     * 管道开始投递
     */
    @Override
    public void start()
    {
        work(null, new RunnerMode(ThreadMode.currentThread, null));
    }
    
    /**
     * 管道开始投递，并且投递的时候携带一个初始参数
     * 
     * @param initParam
     */
    @Override
    public void start(Object initParam)
    {
        work(initParam, new RunnerMode(ThreadMode.currentThread, null));
    }
}
