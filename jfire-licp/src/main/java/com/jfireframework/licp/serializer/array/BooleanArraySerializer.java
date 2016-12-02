package com.jfireframework.licp.serializer.array;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

public class BooleanArraySerializer extends AbstractArraySerializer<boolean[]>
{
    
    public BooleanArraySerializer()
    {
        super(boolean[].class);
    }
    
    @Override
    public void serialize(boolean[] src, ByteBuf<?> buf, Licp licp)
    {
        boolean[] array = src;
        buf.writePositive(array.length);
        for (boolean each : array)
        {
            buf.writeBoolean(each);
        }
    }
    
    @Override
    public boolean[] deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        boolean[] array = new boolean[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readBoolean();
        }
        return array;
    }
    
    @Override
    public boolean[] deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        boolean[] array = new boolean[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = BufferUtil.readBoolean(buf);
        }
        return array;
    }
    
}
