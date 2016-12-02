package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class ShortSerializer implements LicpSerializer<Short>
{
    @Override
    public void serialize(Short src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeShort(src);
    }
    
    @Override
    public Short deserialize(ByteBuf<?> buf, Licp licp)
    {
        Short s = buf.readShort();
        licp.putObject(s);
        return s;
    }
    
    @Override
    public Short deserialize(ByteBuffer buf, Licp licp)
    {
        Short s = BufferUtil.readShort(buf);
        licp.putObject(s);
        return s;
    }
    
}
