package com.jfireframework.baseutil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import com.jfireframework.baseutil.uniqueid.AutumnId;
import com.jfireframework.baseutil.uniqueid.SummerId;
import com.jfireframework.baseutil.uniqueid.Uid;

public class IdTest
{
    
    @Test
    public void test2()
    {
        Uid uid = AutumnId.instance();
        for (int i = 0; i < 10; i++)
        {
            System.out.println(uid.generateDigits());
        }
    }
    
    @Test
    public void test34()
    {
        Uid uid = new SummerId(1);
        for (int i = 0; i < 10; i++)
        {
            System.out.println(uid.generateLong());
        }
        System.out.println(Long.MAX_VALUE);
    }
}
