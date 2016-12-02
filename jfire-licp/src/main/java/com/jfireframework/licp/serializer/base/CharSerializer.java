package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class CharSerializer implements LicpSerializer<Character>
{
    
    @Override
    public void serialize(Character src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeChar(src);
    }
    
    @Override
    public Character deserialize(ByteBuf<?> buf, Licp licp)
    {
        Character c = buf.readChar();
        licp.putObject(c);
        return c;
    }
    
    @Override
    public Character deserialize(ByteBuffer buf, Licp licp)
    {
        Character c = BufferUtil.readChar(buf);
        licp.putObject(c);
        return c;
    }
    
}
