package com.jfireframework.context.test.function.base;

import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.test.function.base.data.ImmutablePerson;
import com.jfireframework.context.test.function.base.data.PropertyReadData;

public class Properties
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.readConfig((JsonObject) JsonTool.fromString(StringUtil.readFromClasspath("propertiestest.json", Charset.forName("utf8"))));
        ImmutablePerson person = jfireContext.getBean(ImmutablePerson.class);
        Assert.assertEquals(12, person.getAge());
    }
    
    @Test
    public void test2()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(PropertyReadData.class.getName(), false, PropertyReadData.class);
        jfireContext.readConfig((JsonObject) JsonTool.fromString(StringUtil.readFromClasspath("propertiestest.json", Charset.forName("utf8"))));
        PropertyReadData data = jfireContext.getBean(PropertyReadData.class);
        Assert.assertEquals(13, data.getAge());
        Assert.assertEquals(10, data.getAge1());
    }
}
