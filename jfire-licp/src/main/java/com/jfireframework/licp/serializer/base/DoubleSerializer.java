package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class DoubleSerializer implements LicpSerializer<Double>
{
    
    @Override
    public void serialize(Double src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeDouble(src);
    }
    
    @Override
    public Double deserialize(ByteBuf<?> buf, Licp licp)
    {
        Double d = buf.readDouble();
        licp.putObject(d);
        return d;
    }
    
    @Override
    public Double deserialize(ByteBuffer buf, Licp licp)
    {
        Double d = BufferUtil.readDouble(buf);
        licp.putObject(d);
        return d;
    }
    
}
