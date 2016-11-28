package com.jfireframework.sql.test;

import java.sql.SQLException;
import org.junit.Before;
import com.jfireframework.sql.session.SessionFactory;
import com.jfireframework.sql.session.impl.SessionFactoryBootstrap;
import com.jfireframework.sql.session.impl.SessionFactoryImpl;
import com.zaxxer.hikari.HikariDataSource;

public class OracleTest
{
    private SessionFactory sessionFactory;
    
    @Before
    public void before()
    {
        HikariDataSource dataSource = new HikariDataSource();
        // dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8");
        dataSource.setJdbcUrl("jdbc:oracle:thin:@192.168.10.21:1521/orcl");
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        // dataSource.setConnectionTestQuery("select count(*) from dual");
        // dataSource.setUsername("root");
        // dataSource.setPassword("centerm");
        dataSource.setUsername("test8");
        dataSource.setPassword("bs");
        dataSource.setMaximumPoolSize(150);
        dataSource.setConnectionTimeout(1500);
        try
        {
            dataSource.getConnection();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sessionFactory = new SessionFactoryImpl(dataSource);
        ((SessionFactoryBootstrap) sessionFactory).setScanPackage("com.jfireframework.sql.test");
        ((SessionFactoryBootstrap) sessionFactory).init();
    }
    
    public void test()
    {
        
    }
}
