package com.jfireframework.licp.serializer.array;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class ObjectArraySerializer<T> extends AbstractArraySerializer<T>
{
    private final LicpSerializer<?> elementSerializer;
    
    public ObjectArraySerializer(Class<T> type, Licp licp)
    {
        super(type);
        if (elementSameType)
        {
            elementSerializer = licp._getSerializer(type.getComponentType());
        }
        else
        {
            elementSerializer = null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void serialize(T src, ByteBuf<?> buf, Licp licp)
    {
        Object[] array = (Object[]) src;
        buf.writePositive(array.length);
        if (elementSameType)
        {
            for (Object each : array)
            {
                licp._serialize(each, buf, (LicpSerializer<Object>) elementSerializer);
            }
        }
        else
        {
            for (Object each : array)
            {
                licp._serialize(each, buf);
            }
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        Object[] array = (Object[]) Array.newInstance(elementType, length);
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            if (elementSameType)
            {
                array[i] = licp._deserialize(buf, elementSerializer);
            }
            else
            {
                array[i] = licp._deserialize(buf);
            }
        }
        return (T) array;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        Object[] array = (Object[]) Array.newInstance(elementType, length);
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            if (elementSameType)
            {
                array[i] = licp._deserialize(buf, elementSerializer);
            }
            else
            {
                array[i] = licp._deserialize(buf);
            }
        }
        return (T) array;
    }
    
}
