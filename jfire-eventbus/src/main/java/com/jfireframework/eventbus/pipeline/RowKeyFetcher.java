package com.jfireframework.eventbus.pipeline;

public interface RowKeyFetcher
{
    /**
     * 从参数对象中得到rowKey
     * 
     * @param data
     * @return
     */
    public Object getRowKey(Object data);
}
