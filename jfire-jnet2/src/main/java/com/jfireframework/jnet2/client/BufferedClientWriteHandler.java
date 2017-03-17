package com.jfireframework.jnet2.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.channel.ClientChannel;

public class BufferedClientWriteHandler implements CompletionHandler<Integer, ByteBuf<?>>
{
    private final Queue<ByteBuf<?>>         sendQueue = new ConcurrentLinkedQueue<>();
    private static final Logger             logger    = ConsoleLogFactory.getLogger();
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer[]              batchBuffers;
    private final ByteBuf<?>[]              batchBufs;
    private final int                       IDLE      = 0;
    private final int                       WORKING   = 1;
    private CpuCachePadingInt               flag      = new CpuCachePadingInt(IDLE);
    private final int                       maxCapacity;
    
    public BufferedClientWriteHandler(ClientChannel clientChannel, int maxCapacity)
    {
        this.maxCapacity = maxCapacity;
        socketChannel = clientChannel.getSocketChannel();
        batchBuffers = new ByteBuffer[maxCapacity];
        batchBufs = new ByteBuf<?>[maxCapacity];
    }
    
    public void writeBuf(ByteBuf<?> buf)
    {
        int status = flag.value();
        if (status == IDLE && flag.compareAndSwap(status, WORKING))
        {
            socketChannel.write(buf.cachedNioBuffer(), buf, this);
        }
        else
        {
            sendQueue.offer(buf);
            status = flag.value();
            if (status == IDLE)
            {
                do
                {
                    if (flag.compareAndSwap(status, WORKING))
                    {
                        batchWrite1();
                        break;
                    }
                    status = flag.value();
                } while (status == IDLE);
            }
        }
    }
    
    @Override
    public void completed(Integer result, ByteBuf<?> buf)
    {
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            socketChannel.write(buffer, buf, this);
            return;
        }
        buf.release();
        batchWrite1();
    }
    
    private void batchWrite1()
    {
        int length = 0;
        for (int i = 0; i < maxCapacity; i++)
        {
            ByteBuf<?> buf = sendQueue.poll();
            if (buf != null)
            {
                batchBufs[i] = buf;
                length += 1;
            }
            else
            {
                break;
            }
        }
        if (length == 0)
        {
            flag.set(IDLE);
            if (sendQueue.isEmpty() == false)
            {
                if (flag.compareAndSwap(IDLE, WORKING))
                {
                    batchWrite1();
                    return;
                }
            }
            return;
        }
        int ca = 0;
        for (int i = 0; i < length; i++)
        {
            ca += batchBufs[i].remainRead();
        }
        ByteBuf<?> send = DirectByteBuf.allocate(ca);
        for (int i = 0; i < length; i++)
        {
            send.put(batchBufs[i]);
            batchBufs[i].release();
        }
        try
        {
            socketChannel.write(send.cachedNioBuffer(), send, this);
        }
        catch (Exception e)
        {
            send.release();
        }
    }
    
    // private void batchWrite()
    // {
    // int length = 0;
    // for (int i = 0; i < maxCapacity; i++)
    // {
    // ByteBuf<?> buf = sendQueue.poll();
    // if (buf != null)
    // {
    // batchBufs[i] = buf;
    // batchBuffers[i] = buf.nioBuffer();
    // length += 1;
    // }
    // else
    // {
    // break;
    // }
    // }
    // if (length == 0)
    // {
    // flag.set(IDLE);
    // if (sendQueue.isEmpty() == false)
    // {
    // if (flag.compareAndSwap(IDLE, WORKING))
    // {
    // batchWrite();
    // return;
    // }
    // }
    // return;
    // }
    // batchWriteHandler.length = length;
    // try
    // {
    // socketChannel.write(batchBuffers, 0, length, 100, TimeUnit.SECONDS,
    // batchBuffers, batchWriteHandler);
    // }
    // catch (Exception e)
    // {
    // for (int i = 0; i < length; i++)
    // {
    // batchBufs[i].release();
    // }
    // }
    // }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        logger.error("error", exc);
        buf.release();
    }
    //
    // class BatchWriteHandler implements CompletionHandler<Long, ByteBuffer[]>
    // {
    // private int length;
    //
    // @Override
    // public void completed(Long result, ByteBuffer[] buffers)
    // {
    // for (int i = 0; i < length; i++)
    // {
    // if (buffers[i].hasRemaining())
    // {
    // socketChannel.write(buffers, i, length - i, 100, TimeUnit.SECONDS,
    // buffers, this);
    // return;
    // }
    // }
    // for (int i = 0; i < length; i++)
    // {
    // batchBufs[i].release();
    // }
    // batchWrite();
    // }
    //
    // @Override
    // public void failed(Throwable exc, ByteBuffer[] buffers)
    // {
    // logger.error("error", exc);
    // for (int i = 0; i < length; i++)
    // {
    // batchBufs[i].release();
    // }
    // }
    //
    // }
    
}
