package com.jfireframework.eventbus.pipeline;

public interface RowKeyFetcher<D>
{
    /**
     * 从参数对象中得到rowKey
     * 
     * @param data
     * @return
     */
    public Object getRowKey(D data);
}
