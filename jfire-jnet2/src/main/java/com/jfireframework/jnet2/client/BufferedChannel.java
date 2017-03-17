package com.jfireframework.jnet2.client;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import com.jfireframework.jnet2.common.channel.ClientChannel;
import com.jfireframework.jnet2.common.channel.impl.AbstractChannel;

public class BufferedChannel extends AbstractChannel implements ClientChannel
{

    public BufferedChannel(AsynchronousSocketChannel socketChannel)
    {
        super(socketChannel);
    }

    @Override
    public void signal(Object obj)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void signalAll(Throwable e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Future<?> addFuture()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
