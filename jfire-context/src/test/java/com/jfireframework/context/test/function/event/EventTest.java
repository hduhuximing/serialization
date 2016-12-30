package com.jfireframework.context.test.function.event;

import java.util.HashMap;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.config.BeanInfo;
import com.jfireframework.context.coordinator.CoordinatorRegisterHelper;

public class EventTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(HaftHandler.class);
        jfireContext.addBean("eventregisterhelper", false, CoordinatorRegisterHelper.class);
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setBeanName("eventregisterhelper");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("eventPath", "com.jfireframework.context.test.function.event");
        beanInfo.setParams(params);
        jfireContext.addBeanInfo(beanInfo);
        jfireContext.initContext();
    }
}
