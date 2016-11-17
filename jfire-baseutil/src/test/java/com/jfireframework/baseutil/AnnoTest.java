package com.jfireframework.baseutil;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;

public class AnnoTest
{
    @Resource
    @Retention(RUNTIME)
    @interface testAnno
    {
        
    }
    
    @testAnno
    class innrtest
    {
        
    }
    
    @Test
    public void test()
    {
        Assert.assertTrue(innrtest.class.isAnnotationPresent(testAnno.class));
        Assert.assertTrue(AnnotationUtil.isPresent(testAnno.class, innrtest.class));
    }
}
