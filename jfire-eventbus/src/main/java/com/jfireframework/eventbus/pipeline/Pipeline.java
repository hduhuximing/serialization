package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.completedhandler.CompletedHandler;

public interface Pipeline
{
    public static final Object USE_UPSTREAM_RESULT = new Object();
    
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
    
    /**
     * 本节点发生异常后调用
     * 
     * @param e
     */
    public void onError(Throwable e);
    
    /**
     * 管道开始投递
     */
    public void start();
    
    /**
     * 管道开始投递，并且投递的时候携带一个初始参数
     * 
     * @param initParam
     */
    public void start(Object initParam);
    
    /**
     * 设置完成触发器
     * 
     * @param completedHandler
     */
    public void setCompletedHanlder(CompletedHandler<Object> completedHandler);
    
    /**
     * 增加一个pipeline到事件编排末尾
     * 
     * @param pipeline
     * @return
     */
    public Pipeline addOperator(final Operator operator);
    
    /**
     * 获取本节点的结果信息
     * 
     * @return
     */
    @Deprecated
    public Object getResult();
    
    /**
     * 获取本节点的异常信息
     * 
     * @return
     */
    @Deprecated
    public Throwable getThrowable();
    
    /**
     * 是否发生过异常
     * 
     * @return
     */
    @Deprecated
    public boolean hasError();
    
    /**
     * 等待本节点的任务本完成.需要注意的是，一个节点只有在调用onCompleted或者onError的时候才会被认为是完成。
     * 在pipeline中的过程节点。未必存在完成这个状态。而有些节点的onCompleted可能被调用不止一次。
     * 因此使用该方法需要注意节点是否最终会完成
     */
    @Deprecated
    public void await();
    
}
