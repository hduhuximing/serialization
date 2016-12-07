package com.jfireframework.eventbus.operator.impl;

public interface FilterOp
{
    /**
     * 是否阻止该事件的后续传递
     * 
     * @param object
     * @return
     */
    public boolean prevent(Object object);
}
