package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class LongSerializer implements LicpSerializer<Long>
{
    
    @Override
    public void serialize(Long src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeVarLong(src);
    }
    
    @Override
    public Long deserialize(ByteBuf<?> buf, Licp licp)
    {
        Long l = buf.readVarLong();
        licp.putObject(l);
        return l;
    }
    
    @Override
    public Long deserialize(ByteBuffer buf, Licp licp)
    {
        Long l = BufferUtil.readVarLong(buf);
        licp.putObject(l);
        return l;
    }
    
}
