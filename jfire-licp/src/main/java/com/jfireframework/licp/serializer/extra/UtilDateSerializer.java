package com.jfireframework.licp.serializer.extra;

import java.nio.ByteBuffer;
import java.util.Date;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class UtilDateSerializer implements LicpSerializer<Date>
{
    
    @Override
    public void serialize(Date src, ByteBuf<?> buf, Licp licp)
    {
        Date date = src;
        buf.writeVarLong(date.getTime());
    }
    
    @Override
    public Date deserialize(ByteBuf<?> buf, Licp licp)
    {
        long time = buf.readVarLong();
        Date result = new Date(time);
        licp.putObject(result);
        return result;
    }
    
    @Override
    public Date deserialize(ByteBuffer buf, Licp licp)
    {
        long time = BufferUtil.readVarLong(buf);
        Date result = new Date(time);
        licp.putObject(result);
        return result;
    }
    
}
