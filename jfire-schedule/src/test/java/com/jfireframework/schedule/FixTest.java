package com.jfireframework.schedule;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;
import com.jfireframework.schedule.handler.impl.SimpleExpireHandler;
import com.jfireframework.schedule.task.Timetask;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.timer.impl.FixedCapacityWheelTimer;
import com.jfireframework.schedule.trigger.impl.FixDayTimeTrigger;
import com.jfireframework.schedule.trigger.impl.RepeatDelayTrigger;

public class FixTest
{
    @Test
    public void test()
    {
        Timer timer = new FixedCapacityWheelTimer(1024, new SimpleExpireHandler(), 10, TimeUnit.MILLISECONDS);
        timer.add(
                new RepeatDelayTrigger(
                        new Timetask() {
                            long t0 = System.currentTimeMillis();
                            
                            @Override
                            public void invoke()
                            {
                                System.out.println(System.currentTimeMillis() - t0);
                                t0 = System.currentTimeMillis();
                            }
                            
                            @Override
                            public boolean canceled()
                            {
                                // TODO Auto-generated method stub
                                return false;
                            }
                        }, 1, TimeUnit.SECONDS
                )
        );
        LockSupport.park();
    }
    
    @Test
    public void test2()
    {
        Timer timer = new FixedCapacityWheelTimer(16, new SimpleExpireHandler(), 1000, TimeUnit.MILLISECONDS);
        timer.add(
                new FixDayTimeTrigger(
                        new Timetask() {
                            long t0 = System.currentTimeMillis();
                            
                            @Override
                            public void invoke()
                            {
                                System.out.println(System.currentTimeMillis() - t0);
                                t0 = System.currentTimeMillis();
                            }
                            
                            @Override
                            public boolean canceled()
                            {
                                // TODO Auto-generated method stub
                                return false;
                            }
                        }, 19, 39, 30
                )
        );
        LockSupport.park();
    }
}
