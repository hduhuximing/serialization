package com.jfireframework.schedule.task;

public interface Timetask
{
    public void invoke();
    
    /**
     * 任务是否已经取消
     * 
     * @return
     */
    public boolean canceled();
}
