package com.jfireframework.context.test.function.initmethod;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.config.BeanInfo;

public class InitMethodTest
{
    
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.initmethod");
        Person person = jfireContext.getBean(Person.class);
        Assert.assertEquals(23, person.getAge());
        Assert.assertEquals("林斌", person.getName());
    }
    
    @Test
    public void testcfg()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.initmethod");
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setBeanName("p2");
        beanInfo.setPostConstructMethod("initage");
        jfireContext.addBeanInfo(beanInfo);
        Person2 person2 = jfireContext.getBean(Person2.class);
        System.out.println("dsasdasd");
        Assert.assertEquals(12, person2.getAge());
    }
    
    @Test
    public void testfilecfg() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.initmethod");
        jfireContext.readConfig((JsonObject) JsonTool.fromString(StringUtil.readFromClasspath("init.json", Charset.forName("utf8"))));
        Person2 person2 = jfireContext.getBean(Person2.class);
        Assert.assertEquals(12, person2.getAge());
    }
}
