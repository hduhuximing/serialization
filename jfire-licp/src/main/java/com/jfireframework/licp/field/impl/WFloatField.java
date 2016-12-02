package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.InternalLicp;
import com.jfireframework.licp.util.BufferUtil;

public class WFloatField extends AbstractCacheField
{
    
    public WFloatField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, InternalLicp licp)
    {
        Float d = (Float) unsafe.getObject(holder, offset);
        if (d == null)
        {
            buf.put((byte) 0);
        }
        else
        {
            buf.put((byte) 1);
            buf.writeFloat(d);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, InternalLicp licp)
    {
        boolean exist = buf.get() == 1 ? true : false;
        if (exist)
        {
            unsafe.putObject(holder, offset, buf.readFloat());
        }
        else
        {
            unsafe.putObject(holder, offset, null);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, InternalLicp licp)
    {
        boolean exist = buf.get() == 1 ? true : false;
        if (exist)
        {
            unsafe.putObject(holder, offset, BufferUtil.readFloat(buf));
        }
        else
        {
            unsafe.putObject(holder, offset, null);
        }
    }
    
}
