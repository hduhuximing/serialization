package com.jfireframework.baseutil;

import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import org.junit.Test;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.time.Timewatch;

public class BenchMarkForModel
{
    private int            offerThreadNum = 8;
    private CyclicBarrier  barrier        = new CyclicBarrier(offerThreadNum + 2);
    private CountDownLatch latch          = new CountDownLatch(1);
    private Queue<String>  queue          = new MPSCQueue<String>();
    private int            sendCount      = 1000000;
    
    @Test
    public void test() throws InterruptedException, BrokenBarrierException
    {
        for (int i = 0; i < offerThreadNum; i++)
        {
            new Thread(new Runnable() {
                
                @Override
                public void run()
                {
                    try
                    {
                        barrier.await();
                        int s = sendCount;
                        for (int i = 0; i < s; i++)
                        {
                            queue.offer("a");
                        }
                    }
                    catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (BrokenBarrierException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        new Thread(new Runnable() {
            
            @Override
            public void run()
            {
                try
                {
                    barrier.await();
                    int sum = sendCount * offerThreadNum;
                    int i = 0;
                    do
                    {
                        if (queue.poll() != null)
                        {
                            i += 1;
                        }
                    } while (i < sum);
                    latch.countDown();
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (BrokenBarrierException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
        Timewatch timewatch = new Timewatch();
        barrier.await();
        timewatch.start();
        latch.await();
        timewatch.end();
        System.out.println(timewatch.getTotal());
    }
}
