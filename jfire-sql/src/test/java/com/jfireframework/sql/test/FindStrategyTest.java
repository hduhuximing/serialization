package com.jfireframework.sql.test;

import java.util.List;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.session.SessionFactory;
import com.jfireframework.sql.session.SqlSession;
import com.jfireframework.sql.session.impl.SessionFactoryImpl;
import com.jfireframework.sql.test.findstrategy.UserStrategy;
import com.zaxxer.hikari.HikariDataSource;

public class FindStrategyTest
{
    private DataSource         dataSource;
    private SessionFactoryImpl sessionFactory;
    
    @Before
    public void before()
    {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:hsqldb:mem:mymemdb");
        // dataSource.setJdbcUrl("jdbc:oracle:thin:@192.168.10.21:1521/orcl");
        // dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUsername("SA");
        dataSource.setPassword("");
        // dataSource.setUsername("test8");
        // dataSource.setPassword("bs");
        dataSource.setMaximumPoolSize(150);
        dataSource.setConnectionTimeout(1500);
        this.dataSource = dataSource;
        sessionFactory = new SessionFactoryImpl(dataSource);
        sessionFactory.setScanPackage("com.jfireframework.sql.test.findstrategy");
        sessionFactory.setTableMode("create");
        sessionFactory.init();
        buildData();
    }
    
    private void buildData()
    {
        SqlSession session = sessionFactory.openSession();
        session.beginTransAction(0);
        for (int i = 1; i < 10; i++)
        {
            UserStrategy user = new UserStrategy();
            user.setAge(i);
            user.setBirthday("2016-10-0" + i);
            user.setBoy(i % 2 == 0);
            user.setName("test-" + i);
            user.setPassword("pass-" + i);
            session.insert(user);
        }
        session.commit();
        session.close();
    }
    
    @Test
    public void test()
    {
        SqlSession session = sessionFactory.openSession();
        UserStrategy user = new UserStrategy();
        user.setAge(5);
        user.setBoy(false);
        UserStrategy result = session.FindOneByStrategy(user, "test1");
        Assert.assertEquals("test-5", result.getName());
        Assert.assertEquals("pass-5", result.getPassword());
        Assert.assertNull(user.getBirthday());
        user = new UserStrategy();
        user.setId(5);
        result = session.FindOneByStrategy(user, "test2");
        Assert.assertNull(result.getPassword());
        Assert.assertEquals("2016-10-06", result.getBirthday());
    }
    
    @Test
    public void test1()
    {
        SqlSession session = sessionFactory.openSession();
        UserStrategy user = new UserStrategy();
        user.setBoy(false);
        List<UserStrategy> result = session.findAllByStrategy(user, "test3");
        for (int i = 1; i <= 5; i++)
        {
            UserStrategy one = result.get(i - 1);
            Assert.assertEquals("pass-" + (i * 2 - 1), one.getPassword());
            Assert.assertEquals((i - 1) * 2, one.getId().intValue());
            Assert.assertNull(one.getBirthday());
            Assert.assertEquals(i * 2 - 1, one.getAge().intValue());
        }
    }
    
    @Test
    public void test2()
    {
        Page page = new Page();
        page.setPage(1);
        page.setPageSize(2);
        SqlSession session = sessionFactory.openSession();
        UserStrategy user = new UserStrategy();
        user.setBoy(false);
        List<UserStrategy> result = session.findPageByStrategy(user, "test3", page);
        for (int i = 1; i <= 2; i++)
        {
            UserStrategy one = result.get(i - 1);
            Assert.assertEquals("pass-" + (i * 2 - 1), one.getPassword());
            Assert.assertEquals((i - 1) * 2, one.getId().intValue());
            Assert.assertNull(one.getBirthday());
            Assert.assertEquals(i * 2 - 1, one.getAge().intValue());
        }
        Assert.assertEquals(2, page.getData().size());
        Assert.assertEquals(5, page.getTotal());
    }
}
