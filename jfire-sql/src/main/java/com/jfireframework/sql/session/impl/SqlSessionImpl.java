package com.jfireframework.sql.session.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.sql.dao.LockMode;
import com.jfireframework.sql.interceptor.SqlInterceptor;
import com.jfireframework.sql.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.interceptor.SqlInterceptor.InterceptorContext;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.page.PageParse;
import com.jfireframework.sql.resultsettransfer.ResultSetTransfer;
import com.jfireframework.sql.session.SessionFactory;
import com.jfireframework.sql.session.SqlSession;

public class SqlSessionImpl implements SqlSession
{
    private int                       transNum = 0;
    private int                       autoOpen = 0;
    private boolean                   closed   = false;
    private final Connection          connection;
    private final SessionFactory      sessionFactory;
    private final static Logger       logger   = ConsoleLogFactory.getLogger();
    private final SqlPreInterceptor[] preInterceptors;
    private final SqlInterceptor[]    sqlInterceptors;
    private final PageParse           pageParse;
    
    public SqlSessionImpl(Connection connection, SessionFactory sessionFactory, SqlPreInterceptor[] preInterceptors, SqlInterceptor[] sqlInterceptors, PageParse pageParse)
    {
        logger.trace("打开sqlsession");
        this.connection = connection;
        this.sessionFactory = sessionFactory;
        this.preInterceptors = preInterceptors;
        this.sqlInterceptors = sqlInterceptors;
        this.pageParse = pageParse;
    }
    
    @Override
    public void beginTransAction(int isolate)
    {
        try
        {
            if (transNum == 0)
            {
                transNum++;
                connection.setAutoCommit(false);
                if (isolate > 0)
                {
                    connection.setTransactionIsolation(isolate);
                }
            }
            else
            {
                transNum++;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void commit()
    {
        try
        {
            transNum--;
            if (transNum == 0)
            {
                connection.commit();
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e)
        {
            logger.error("事务提交出现异常，请确认当前连接是否仍然还在事务内。请不要在一个事务内开启两个连接");
            throw new RuntimeException(e);
        }
    }
    
    public void autoOpen()
    {
        autoOpen += 1;
    }
    
    public void autoClose()
    {
        autoOpen -= 1;
        if (autoOpen == 0)
        {
            close();
        }
    }
    
    @Override
    public void flush()
    {
        try
        {
            connection.commit();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void rollback()
    {
        try
        {
            transNum--;
            if (transNum == 0)
            {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close()
    {
        if (closed || transNum > 0 || autoOpen > 0)
        {
            return;
        }
        try
        {
            closed = true;
            sessionFactory.removeCurrentSession();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("关闭", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> int delete(T entity)
    {
        return sessionFactory.getDao((Class<T>) entity.getClass()).delete(entity, connection);
    }
    
    @Override
    public <T> T get(Class<T> entityClass, Object pk)
    {
        return sessionFactory.getDao(entityClass).getById(pk, connection);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void save(T entity)
    {
        sessionFactory.getDao((Class<T>) entity.getClass()).save(entity, connection);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void batchInsert(List<T> entitys)
    {
        sessionFactory.getDao((Class<T>) entitys.get(0).getClass()).batchInsert(entitys, connection);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> void insert(T entity)
    {
        sessionFactory.getDao((Class<T>) entity.getClass()).insert(entity, connection);
    }
    
    @Override
    public Connection getConnection()
    {
        return connection;
    }
    
    @Override
    public <T> T get(Class<T> entityClass, Object pk, LockMode mode)
    {
        return sessionFactory.getDao(entityClass).getById(pk, connection, mode);
    }
    
    @Override
    public <T> T findBy(Class<T> entityClass, String name, Object param)
    {
        return sessionFactory.getDao(entityClass).findBy(connection, param, name);
    }
    
    @Override
    public <T> T query(ResultSetTransfer<T> transfer, String sql, Object... params)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            sql = each.preIntercept(sql, params);
        }
        PreparedStatement pstat = null;
        ResultSet resultSet = null;
        try
        {
            if (sqlInterceptors.length != 0)
            {
                InterceptorContext context = new InterceptorContext();
                context.setSql(sql);
                context.setParams(params);
                for (SqlInterceptor each : sqlInterceptors)
                {
                    each.intercept(context);
                }
                sql = context.getSql();
                params = context.getParams();
            }
            pstat = connection.prepareStatement(sql);
            int index = 1;
            for (Object each : params)
            {
                pstat.setObject(index++, each);
            }
            resultSet = pstat.executeQuery();
            return transfer.transfer(resultSet, sql);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (pstat != null)
                {
                    pstat.close();
                }
            }
            catch (SQLException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    @Override
    public <T> List<T> queryList(ResultSetTransfer<T> transfer, String sql, Object... params)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            sql = each.preIntercept(sql, params);
        }
        PreparedStatement pstat = null;
        ResultSet resultSet = null;
        try
        {
            if (sqlInterceptors.length != 0)
            {
                InterceptorContext context = new InterceptorContext();
                context.setSql(sql);
                context.setParams(params);
                for (SqlInterceptor each : sqlInterceptors)
                {
                    each.intercept(context);
                }
                sql = context.getSql();
                params = context.getParams();
            }
            pstat = connection.prepareStatement(sql);
            int index = 1;
            for (Object each : params)
            {
                pstat.setObject(index++, each);
            }
            resultSet = pstat.executeQuery();
            return transfer.transferList(resultSet, sql);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (pstat != null)
                {
                    pstat.close();
                }
            }
            catch (SQLException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> queryList(ResultSetTransfer<T> transfer, String sql, Page page, Object... params)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            sql = each.preIntercept(sql, params);
        }
        try
        {
            if (sqlInterceptors.length != 0)
            {
                InterceptorContext context = new InterceptorContext();
                context.setSql(sql);
                context.setParams(params);
                for (SqlInterceptor each : sqlInterceptors)
                {
                    each.intercept(context);
                }
                sql = context.getSql();
                params = context.getParams();
            }
            pageParse.doQuery(params, connection, sql, transfer, page);
            return (List<T>) page.getData();
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    @Override
    public int update(String sql, Object... params)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            sql = each.preIntercept(sql, params);
        }
        PreparedStatement pstat = null;
        ResultSet resultSet = null;
        try
        {
            if (sqlInterceptors.length != 0)
            {
                InterceptorContext context = new InterceptorContext();
                context.setSql(sql);
                context.setParams(params);
                for (SqlInterceptor each : sqlInterceptors)
                {
                    each.intercept(context);
                }
                sql = context.getSql();
                params = context.getParams();
            }
            pstat = connection.prepareStatement(sql);
            int index = 1;
            for (Object each : params)
            {
                pstat.setObject(index++, each);
            }
            return pstat.executeUpdate();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (pstat != null)
                {
                    pstat.close();
                }
            }
            catch (SQLException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T FindOneByStrategy(T entity, String strategyName)
    {
        return sessionFactory.getDao((Class<T>) entity.getClass()).findOne(connection, entity, strategyName);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllByStrategy(T entity, String steategyName)
    {
        return sessionFactory.getDao((Class<T>) entity.getClass()).findAll(connection, entity, steategyName);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findPageByStrategy(T entity, String steategyName, Page page)
    {
        return sessionFactory.getDao((Class<T>) entity.getClass()).findPage(connection, entity, steategyName, page, pageParse);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> int updateByStrategy(T entity, String strategyName)
    {
        return sessionFactory.getDao((Class<T>) entity.getClass()).update(entity, connection, strategyName);
    }
    
}
