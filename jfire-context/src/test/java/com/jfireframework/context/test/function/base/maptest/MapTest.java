package com.jfireframework.context.test.function.base.maptest;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class MapTest
{
    @Test
    public void test() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.readConfig((JsonObject) JsonTool.fromString(StringUtil.readFromClasspath("mapconfig.json", Charset.forName("utf8"))));
        House house = jfireContext.getBean(House.class);
        Map<String, Person> map = house.getMap();
        Assert.assertEquals(2, map.size());
        
    }
}
