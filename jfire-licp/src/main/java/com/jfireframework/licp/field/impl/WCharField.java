package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.InternalLicp;
import com.jfireframework.licp.util.BufferUtil;

public class WCharField extends AbstractCacheField
{
    
    public WCharField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, InternalLicp licp)
    {
        Character character = (Character) unsafe.getObject(holder, offset);
        if (character == null)
        {
            buf.put((byte) 0);
        }
        else
        {
            buf.put((byte) 1);
            buf.writeChar(character);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, InternalLicp licp)
    {
        boolean exist = buf.get() == 1 ? true : false;
        if (exist == false)
        {
            unsafe.putObject(holder, offset, null);
        }
        else
        {
            unsafe.putObject(holder, offset, buf.readChar());
        }
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, InternalLicp licp)
    {
        boolean exist = buf.get() == 1 ? true : false;
        if (exist == false)
        {
            unsafe.putObject(holder, offset, null);
        }
        else
        {
            unsafe.putObject(holder, offset, BufferUtil.readChar(buf));
        }
    }
    
}
