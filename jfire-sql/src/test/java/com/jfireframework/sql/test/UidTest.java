package com.jfireframework.sql.test;

import org.junit.Test;
import com.jfireframework.sql.test.entity.UidUser;

public class UidTest extends BaseTestSupport
{
    @Test
    public void test()
    {
        UidUser user = new UidUser();
        user.setUsername("test");
        session.save(user);
    }
}
