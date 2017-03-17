package com.jfireframework.jnet2.client;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.ClientChannel;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.common.result.InternalResultImpl;

public class BufferAioClient
{
    private ClientChannel              clientChannel;
    private String                     address;
    private int                        port;
    private AsynchronousChannelGroup   channelGroup;
    private DataHandler[]              writeHandlers;
    private ChannelInitListener        initListener;
    private final InternalResult       internalResult = new InternalResultImpl();
    private int                        capacity       = 16;
    private long                       connectTimeout = 10;
    private final int                  retryLimit     = 30;
    private BufferedClientWriteHandler writeHandler;
    private final int                  maxCapacity;
    
    public BufferAioClient(int maxCapacity)
    {
        this.maxCapacity = maxCapacity;
    }
    
    public BufferAioClient()
    {
        this(64);
    }
    
    public int getCapacity()
    {
        return capacity;
    }
    
    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }
    
    public BufferAioClient setAddress(String address)
    {
        this.address = address;
        return this;
    }
    
    public BufferAioClient setPort(int port)
    {
        this.port = port;
        return this;
    }
    
    public void setInitListener(ChannelInitListener initListener)
    {
        this.initListener = initListener;
    }
    
    public BufferAioClient setChannelGroup(AsynchronousChannelGroup channelGroup)
    {
        this.channelGroup = channelGroup;
        return this;
    }
    
    public BufferAioClient setWriteHandlers(DataHandler... writeHandlers)
    {
        this.writeHandlers = writeHandlers;
        return this;
    }
    
    public BufferAioClient connect() throws Throwable
    {
        if (clientChannel == null || clientChannel.isOpen() == false)
        {
            int retryCount = 0;
            do
            {
                try
                {
                    AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(channelGroup);
                    socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                    socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                    socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                    socketChannel.connect(new InetSocketAddress(address, port)).get(connectTimeout, TimeUnit.SECONDS);
                    clientChannel = new BufferedChannel(socketChannel);
                    initListener.channelInit(clientChannel);
                    Verify.notNull(clientChannel.getFrameDecodec(), "没有设置framedecodec");
                    Verify.notNull(clientChannel.getHandlers(), "没有设置Datahandler");
                    ClientReadCompleter clientReadCompleter = new ClientReadCompleter(clientChannel);
                    clientReadCompleter.readAndWait();
                    writeHandler = new BufferedClientWriteHandler(clientChannel, maxCapacity);
                    return this;
                }
                catch (Exception e)
                {
                    retryCount += 1;
                    Thread.sleep(1000);
                }
            } while (retryCount < retryLimit);
            throw new RuntimeException("重试次数内，无法连接客户端");
        }
        return this;
    }
    
    /**
     * 将一个对象写出并且返回一个future。该future表明的是服务端对该请求报文的响应报文的处理结果
     * 
     * @param object
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JnetException
     */
    public void write(Object object) throws Throwable
    {
        write(object, 0);
    }
    
    /**
     * 将一个对象写出并且指定开始处理时的handler顺序，然后返回一个future。该future表明的是服务端对该请求报文的响应报文的处理结果
     * 
     * @param object
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JnetException
     */
    public void write(Object data, int index) throws Throwable
    {
        try
        {
            if (clientChannel == null || clientChannel.isOpen() == false)
            {
                throw new InterruptedException("链接已经中断，请重新链接后再发送信息");
            }
            internalResult.setIndex(0);
            internalResult.setChannelInfo(clientChannel);
            for (int i = index; i < writeHandlers.length;)
            {
                data = writeHandlers[i].handle(data, internalResult);
                if (i == internalResult.getIndex())
                {
                    i++;
                    internalResult.setIndex(i);
                }
                else
                {
                    i = internalResult.getIndex();
                }
            }
            if (data instanceof ByteBuf<?>)
            {
                writeHandler.writeBuf((ByteBuf<?>) data);
            }
        }
        catch (Exception e)
        {
            Object tmp = e;
            internalResult.setIndex(0);
            internalResult.setChannelInfo(clientChannel);
            internalResult.setData(e);
            for (DataHandler each : writeHandlers)
            {
                tmp = each.catchException(tmp, internalResult);
            }
            close();
            if (tmp instanceof Throwable)
            {
                throw (Throwable) tmp;
            }
            else
            {
                throw e;
            }
        }
    }
    
    public void close()
    {
        clientChannel.closeChannel();
    }
}
