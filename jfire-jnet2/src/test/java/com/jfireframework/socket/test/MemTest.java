package com.jfireframework.socket.test;

import java.util.concurrent.locks.LockSupport;
import javax.sound.sampled.Port;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.jnet2.client.AioClient;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.JnetChannel;
import com.jfireframework.jnet2.common.decodec.LineBasedFrameDecodec;
import com.jfireframework.jnet2.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.util.AcceptMode;
import com.jfireframework.jnet2.server.util.ExecutorMode;
import com.jfireframework.jnet2.server.util.PushMode;
import com.jfireframework.jnet2.server.util.ServerConfig;
import com.jfireframework.jnet2.server.util.WorkMode;

public class MemTest
{
    int port  = 5896;
    int count = 10000;
    
    @Test
    public void test()
    {
        ServerConfig config = new ServerConfig();
        config.setAcceptMode(AcceptMode.CAPACITY);
        config.setPushMode(PushMode.OFF);
        config.setWorkMode(WorkMode.SYNC);
        config.setSocketThreadNum(4);
        config.setAsyncCapacity(16);
        config.setChannelCapacity(64);
        config.setExecutorMode(ExecutorMode.FIX);
        config.setAsyncThreadNum(16);
        config.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel serverChannelInfo)
            {
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                serverChannelInfo.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws Throwable
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });
            }
        });
        config.setLocalTestMode(false);
        config.setPort(port);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        System.out.println("total:" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M,free:" + Runtime.getRuntime().freeMemory() / 1024 / 1024);
        Timewatch timewatch = new Timewatch();
        DataHandler wr = new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws JnetException
            {
                String value = (String) data;
                ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                buf.writeString(value);
                buf.put((byte) '\r');
                buf.put((byte) '\n');
                return buf;
            }
            
            @Override
            public Object catchException(Object data, InternalResult result)
            {
                // TODO Auto-generated method stub
                return null;
            }
        };
        ChannelInitListener initListener = new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel jnetChannel)
            {
                // TODO Auto-generated method stub
                jnetChannel.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 0, 0, 0));
                jnetChannel.setHandlers(new DataHandler(){

                    @Override
                    public Object handle(Object data, InternalResult result) throws Throwable
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }});
            }
        };
        ;
        for (int i = 0; i < count; i++)
        {
            AioClient aioClient = new AioClient(false);
            aioClient.setAddress("127.0.0.1").setPort(port);
            aioClient.setWriteHandlers(wr);
            aioClient.setInitListener(initListener);
            // 使用对应的参数链接服务端
            try
            {
                aioClient.connect();
            }
            catch (Throwable e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        timewatch.end();
        System.out.println("链接耗时：" + timewatch.getTotal());
        System.out.println("total:" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M,free:" + Runtime.getRuntime().freeMemory() / 1024 / 1024);
    }
}
