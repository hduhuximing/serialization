package com.jfireframework.eventbus.eventcontext;

import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.executor.EventExecutor;
import com.jfireframework.eventbus.util.RunnerMode;

public interface EventContext<T> extends Runnable
{
    public RunnerMode runnerMode();
    
    /**
     * 返回该事件绑定执行器
     * 
     * @return
     */
    public EventExecutor executor();
    
    /**
     * 返回该事件绑定的处理器
     * 
     * @return
     */
    public EventHandler<?> eventHandler();
    
    /**
     * 等待直到该事件被处理完成
     */
    public void await();
    
    /**
     * 事件发生过程中发生了异常。记录异常并且让事件完成
     * 
     * @param e
     */
    public void setThrowable(Throwable e);
    
    /**
     * 本事件处理过程中捕获的异常
     * 
     * @return
     */
    public Throwable getThrowable();
    
    /**
     * 获取事件处理的结果数据,如果事件没有完成。就阻塞到完成为止
     * 
     * @return
     */
    public T getResult();
    
    /**
     * 获取事件处理的结果数据。如果事件没有完成，就阻塞到事件完成为止或者超时退出 如果超时时间到达还没有完成，抛出异常
     * 
     * @return
     */
    public T getResult(long mills) throws InterruptedException;
    
    /**
     * 
     * 完成该事件，并且（如果有）唤醒等待该事件完成的线程
     */
    public void setResult(Object result);
    
    /**
     * 等待该事件的完成，最多等待指定的毫秒数
     * 
     * @param mills
     */
    public void await(long mills);
    
    /**
     * 事件是否完成
     * 
     * @return
     */
    public boolean isFinished();
    
    /**
     * 返回事件的待处理数据
     * 
     * @return
     */
    public Object getEventData();
    
}
