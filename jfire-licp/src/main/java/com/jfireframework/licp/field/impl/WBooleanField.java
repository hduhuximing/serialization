package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.licp.InternalLicp;

public class WBooleanField extends AbstractCacheField
{
    
    public WBooleanField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, InternalLicp licp)
    {
        Boolean value = (Boolean) unsafe.getObject(holder, offset);
        if (value == null)
        {
            buf.put((byte) 0);
        }
        else if (value == true)
        {
            buf.put((byte) 1);
        }
        else if (value == false)
        {
            buf.put((byte) 2);
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, InternalLicp licp)
    {
        byte b = buf.get();
        if (b == 0)
        {
            unsafe.putObject(holder, offset, null);
        }
        else if (b == 1)
        {
            unsafe.putObject(holder, offset, true);
        }
        else if (b == 2)
        {
            unsafe.putObject(holder, offset, false);
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, InternalLicp licp)
    {
        byte b = buf.get();
        if (b == 0)
        {
            unsafe.putObject(holder, offset, null);
        }
        else if (b == 1)
        {
            unsafe.putObject(holder, offset, true);
        }
        else if (b == 2)
        {
            unsafe.putObject(holder, offset, false);
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
}
