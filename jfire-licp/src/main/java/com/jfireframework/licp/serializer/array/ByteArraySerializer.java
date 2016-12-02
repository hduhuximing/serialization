package com.jfireframework.licp.serializer.array;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

public class ByteArraySerializer extends AbstractArraySerializer<byte[]>
{
    
    public ByteArraySerializer()
    {
        super(byte[].class);
    }
    
    @Override
    public void serialize(byte[] src, ByteBuf<?> buf, Licp licp)
    {
        byte[] array = src;
        buf.writePositive(array.length);
        for (byte each : array)
        {
            buf.put(each);
        }
    }
    
    @Override
    public byte[] deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        byte[] array = new byte[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.get();
        }
        return array;
    }
    
    @Override
    public byte[] deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        byte[] array = new byte[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.get();
        }
        return array;
    }
    
}
