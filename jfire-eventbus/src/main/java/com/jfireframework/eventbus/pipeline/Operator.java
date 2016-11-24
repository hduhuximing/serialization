package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.util.RunnerMode;

public interface Operator
{
    /**
     * 当本pipeline被激活的时候会调用的方法。传入上游的结果数据，本节点pipeline，以及上下文的runnerMode
     * 
     * @param data
     * @param pipeline
     * @param runnerMode
     */
    public void work(Object data, Pipeline pipeline, RunnerMode runnerMode);
    
    /**
     * 当本节点发生异常的时候会被调用的方法。入参的值和pipeline.onError相同
     * 
     * @param e
     * @param runnerMode
     */
    public void onError(Throwable e, RunnerMode runnerMode);
}
