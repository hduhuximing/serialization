package com.jfireframework.coordinator.handlercontext;

public interface RowCoordinatorContext<T> extends CoordinatorContext<T>
{
    /**
     * 返回一个用于标识本行数据的key
     * 
     * @return
     */
    public Object rowkey();
}
