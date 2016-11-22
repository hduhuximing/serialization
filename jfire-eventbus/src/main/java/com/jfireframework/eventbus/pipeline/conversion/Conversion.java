package com.jfireframework.eventbus.pipeline.conversion;

import com.jfireframework.eventbus.pipeline.Pipeline;

public interface Conversion<E>
{
    /**
     * 转化是否完成
     * 
     * @param data
     * @param pipeline
     * @return
     */
    public boolean conversie(E data, Pipeline pipeline);
}
