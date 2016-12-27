package com.jfireframework.eventbus.operator;

import com.jfireframework.eventbus.pipeline.Pipeline;

public interface TransferOp
{
    public Pipeline transfer(Pipeline pipeline);
}
