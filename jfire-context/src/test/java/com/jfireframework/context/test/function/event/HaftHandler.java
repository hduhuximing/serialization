package com.jfireframework.context.test.function.event;

import javax.annotation.Resource;
import com.jfireframework.context.ContextInitFinish;
import com.jfireframework.context.event.EventRegisterHelper;
import com.jfireframework.eventbus.bus.EventBuses;
import com.jfireframework.eventbus.event.EventHandler;
import com.jfireframework.eventbus.util.RunnerMode;

@Resource
public class HaftHandler implements EventHandler<UserPhone>, ContextInitFinish
{
    @Resource
    private EventRegisterHelper publisher;
    
    @Override
    public Object handle(UserPhone myEvent, RunnerMode runnerMode)
    {
        System.out.println("asdasd");
        System.out.println("用户:" + myEvent.getPhone() + "欠费");
        return null;
    }
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public void afterContextInit()
    {
        UserPhone phone = new UserPhone();
        phone.setPhone("1775032");
        EventBuses.computation().post(phone, SmsEvent.halt, this).await();
    }
    
}
