package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class ByteSerializer implements LicpSerializer<Byte>
{
    
    @Override
    public void serialize(Byte src, ByteBuf<?> buf, Licp licp)
    {
        buf.put(src);
    }
    
    @Override
    public Byte deserialize(ByteBuf<?> buf, Licp licp)
    {
        Byte b = buf.get();
        licp.putObject(b);
        return b;
    }
    
    @Override
    public Byte deserialize(ByteBuffer buf, Licp licp)
    {
        Byte b = buf.get();
        licp.putObject(b);
        return b;
    }
    
}
