package com.jfireframework.sql.test;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.dbunit.schema.DbUnit;
import com.jfireframework.sql.session.SqlSession;
import com.jfireframework.sql.session.impl.SessionFactoryImpl;
import com.zaxxer.hikari.HikariDataSource;

public abstract class BaseTestSupport
{
    protected static DbUnit             testUnit;
    protected static SessionFactoryImpl sessionFactory;
    protected SqlSession                session;
    protected Connection                connection;
    protected Logger                    logger = ConsoleLogFactory.getLogger();
    
    static
    {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8");
        // dataSource.setJdbcUrl("jdbc:oracle:thin:@192.168.10.21:1521/orcl");
        // dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("centerm");
        // dataSource.setUsername("test8");
        // dataSource.setPassword("bs");
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
        sessionFactory.setScanPackage("com.jfireframework.sql.test");
        sessionFactory.init();
        testUnit = new DbUnit(DbUnit.SAVE_IN_MEM, dataSource);
    }
    
    public BaseTestSupport()
    {
        testUnit.clearSchemaData();
        testUnit.importExcelFile();
    }
    
    @Before
    public void before()
    {
        testUnit.clearSchemaData();
        testUnit.importExcelFile();
        session = sessionFactory.getOrCreateCurrentSession();
        connection = session.getConnection();
    }
    
    @After
    public void after()
    {
        testUnit.clearSchemaData();
        testUnit.restoreSchemaData();
        session.close();
    }
    
}
