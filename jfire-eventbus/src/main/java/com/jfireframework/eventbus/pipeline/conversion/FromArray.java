package com.jfireframework.eventbus.pipeline.conversion;

import com.jfireframework.eventbus.pipeline.Pipeline;

public class FromArray<E> implements Conversion<E>
{
    
    @Override
    public boolean conversie(E data, Pipeline pipeline)
    {
        if (data instanceof int[])
        {
            for (int i : (int[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof byte[])
        {
            for (byte i : (byte[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof short[])
        {
            for (short i : (short[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof long[])
        {
            for (long i : (long[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof float[])
        {
            for (float i : (float[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof double[])
        {
            for (double i : (double[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof boolean[])
        {
            for (boolean i : (boolean[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else if (data instanceof char[])
        {
            for (char i : (char[]) data)
            {
                pipeline.onCompleted(i);
            }
        }
        else
        {
            for (Object each : (Object[]) data)
            {
                pipeline.onCompleted(each);
            }
        }
        return true;
    }
    
}
