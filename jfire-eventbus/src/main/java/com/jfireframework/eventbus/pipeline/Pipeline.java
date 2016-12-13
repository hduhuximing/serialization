package com.jfireframework.eventbus.pipeline;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.operator.MapOp;
import com.jfireframework.eventbus.operator.Operator;
import com.jfireframework.eventbus.operator.OperatorData;
import com.jfireframework.eventbus.operator.TransferOp;
import com.jfireframework.eventbus.operator.impl.FilterOp;
import com.jfireframework.eventbus.util.Schedules;

/**
 * 用户提供pipeline的连接操作
 * 
 * @author linbin
 *
 */
public interface Pipeline
{
    public Pipeline next(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, Object rowKey);
    
    public Pipeline next(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData);
    
    public Pipeline next(Enum<? extends EventConfig> event, EventHandler<?> handler, Object eventData, RowKeyFetcher<?> rowKeyFetcher);
    
    public Pipeline next(Enum<? extends EventConfig> event, EventHandler<?> handler);
    
    public Pipeline next(Enum<? extends EventConfig> event, EventHandler<?> handler, RowKeyFetcher<?> fetcher);
    
    public Pipeline next(EventHandler<?> handler);
    
    /**
     * 增加一个from操作的节点。该节点可以将数组以及集合中的元素以单个元素的方式不断的传递给后续
     * 
     * @return
     */
    public Pipeline from();
    
    /**
     * 增加一个拦截节点
     * 
     * @param filterOp
     * @return
     */
    public Pipeline filter(FilterOp filterOp);
    
    /**
     * 增加一个转换节点
     * 
     * @param mapOp
     * @return
     */
    public <E> Pipeline map(final MapOp<E> mapOp);
    
    /**
     * 将后续节点的执行切换到指定的eventbus上
     * 
     * @param eventBus
     * @return
     */
    public Pipeline switchTo(final EventBus eventBus);
    
    public Pipeline switchTo(Schedules schedules);
    
    public Pipeline distribute(final OperatorData... datas);
    
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
    
    public Pipeline add(Operator operator);
    
    public Pipeline compose(TransferOp transferOp);
}
