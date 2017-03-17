package com.jfireframework.socket.test;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.charset.Charset;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.jnet2.ComListener;
import com.jfireframework.jnet2.client.BufferAioClient;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.JnetChannel;
import com.jfireframework.jnet2.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.handler.LengthPreHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.write.withoutpush.WeaponCapacityWriteHandlerImpl;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.write.withoutpush.SyncSingleWriteHandlerImpl;
import com.jfireframework.jnet2.server.util.AcceptMode;
import com.jfireframework.jnet2.server.util.ExecutorMode;
import com.jfireframework.jnet2.server.util.PushMode;
import com.jfireframework.jnet2.server.util.ServerConfig;
import com.jfireframework.jnet2.server.util.WorkMode;

public class NewSpeedTest
{
    int                                port      = 8553;
    int                                sendCount = 100000;
    int                                index     = 100;
    int                                capacity  = 1024;
    int                                sum       = sendCount * index;
    byte[]                             content   = "hello world".getBytes(Charset.forName("utf8"));
    private static final AtomicInteger count     = new AtomicInteger(0);
    
    @Test
    public void test() throws InterruptedException, BrokenBarrierException, IOException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        ServerConfig config = new ServerConfig();
        config.setAcceptMode(AcceptMode.CAPACITY);
        config.setPushMode(PushMode.OFF);
        config.setWorkMode(WorkMode.SYNC);
        config.setSocketThreadNum(40);
        config.setAsyncCapacity(16);
        config.setChannelCapacity(capacity);
        config.setExecutorMode(ExecutorMode.FIX);
        config.setAsyncThreadNum(0);
        WeaponCapacityWriteHandlerImpl.comListener = new ComListener() {
            
            @Override
            public void on(int total)
            {
                int value = count.addAndGet(total);
                if (value == sum)
                {
                    latch.countDown();
                }
            }
        };
        SyncSingleWriteHandlerImpl.comListener = new ComListener() {
            
            @Override
            public void on(int total)
            {
                int value = count.addAndGet(total);
                if (value == sum)
                {
                    latch.countDown();
                }
            }
        };
        config.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel serverChannelInfo)
            {
                serverChannelInfo.setReadTimeout(30 * 1000);
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                serverChannelInfo.setHandlers(new DataHandler() {
                    @Override
                    public Object handle(Object data, InternalResult result) throws Throwable
                    {
                        ByteBuf<?> buf = (ByteBuf<?>) data;
                        buf.readIndex(0);
                        return buf;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        Throwable e = (Throwable) data;
                        e.printStackTrace();
                        return null;
                    }
                });
            }
        });
        config.setPort(port);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        ExecutorService pool = Executors.newFixedThreadPool(index);
        final CyclicBarrier barrier = new CyclicBarrier(index + 1);
        final AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(index, new ThreadFactory() {
            int i = 0;
            
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "客户端-" + (i++));
            }
        });
        for (int i = 0; i < index; i++)
        {
            pool.submit(new Runnable() {
                
                @Override
                public void run()
                {
                    try
                    {
                        connecttest(barrier, channelGroup);
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        Timewatch timewatch = new Timewatch();
        barrier.await();
        timewatch.start();
        latch.await();
        timewatch.end();
        System.out.println("线程数量：" + index + "发送消息:" + index * sendCount + ",运行完毕:" + timewatch.getTotal());
    }
    
    public void connecttest(CyclicBarrier barrier, AsynchronousChannelGroup channelGroup) throws Throwable
    {
        BufferAioClient client = new BufferAioClient(capacity);
        client.setChannelGroup(channelGroup);
        client.setAddress("127.0.0.1");
        client.setPort(port);
        client.setWriteHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws JnetException
            {
                ByteBuf<?> buf = DirectByteBuf.allocate(100);
                byte[] data1 = (byte[]) data;
                buf.writeInt(data1.length + 4);
                buf.put(data1);
                return buf;
            }
            
            @Override
            public Object catchException(Object data, InternalResult result)
            {
                ((Throwable) data).printStackTrace();
                return data;
            }
        });
        client.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel jnetChannel)
            {
                jnetChannel.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                jnetChannel.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        // System.out.println("收到数据");
                        return null;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // System.err.println("客户端");
                        ((Throwable) data).printStackTrace();
                        return data;
                    }
                });
            }
        });
        client.connect();
        barrier.await();
        for (int i = 0; i < sendCount; i++)
        {
            client.write(content);
            Thread.sleep(5);
        }
    }
}
