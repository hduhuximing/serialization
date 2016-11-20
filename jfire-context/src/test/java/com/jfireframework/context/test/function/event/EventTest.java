package com.jfireframework.context.test.function.event;

import java.util.HashMap;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.config.BeanInfo;
import com.jfireframework.context.event.impl.IoEventPoster;

public class EventTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(HaftHandler.class);
        jfireContext.addBean("com.jfireframework.context.event.impl.EventPosterImpl", false, IoEventPoster.class);
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setBeanName("com.jfireframework.context.event.impl.EventPosterImpl");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("events", "com.jfireframework.context.test.function.event.SmsEvent");
        beanInfo.setParams(params);
        jfireContext.addBeanInfo(beanInfo);
        jfireContext.initContext();
    }
}
