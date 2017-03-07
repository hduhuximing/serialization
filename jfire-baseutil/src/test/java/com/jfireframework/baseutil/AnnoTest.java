package com.jfireframework.baseutil;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.aliasanno.AliasFor;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;

public class AnnoTest
{
    @Resource
    @Retention(RUNTIME)
    public static @interface testAnno
    {
        
    }
    
    @testAnno
    @level2value("levle2")
    public static class innrtest
    {
        
    }
    
    @Retention(RUNTIME)
    public static @interface level1value
    {
        public String value();
    }
    
    @Retention(RUNTIME)
    @level1value("level1")
    public static @interface level2value
    {
        @AliasFor(value = "value", annotation = level1value.class)
        public String value();
    }
    
    @Test
    public void test()
    {
        Assert.assertTrue(innrtest.class.isAnnotationPresent(testAnno.class));
        Assert.assertTrue(AnnotationUtil.isPresent(testAnno.class, innrtest.class));
        Assert.assertEquals("levle2", AnnotationUtil.getAnnotation(level1value.class, innrtest.class).value());
    }
}
