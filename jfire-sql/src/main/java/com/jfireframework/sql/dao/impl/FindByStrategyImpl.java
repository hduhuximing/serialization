package com.jfireframework.sql.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.annotation.FindStrategy;
import com.jfireframework.sql.annotation.SqlStrategy;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.dao.FindByStrategy;
import com.jfireframework.sql.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.page.PageParse;
import com.jfireframework.sql.resultsettransfer.FixBeanTransfer;
import com.jfireframework.sql.resultsettransfer.ResultSetTransfer;
import com.jfireframework.sql.resultsettransfer.field.MapField;

public class FindByStrategyImpl<T> implements FindByStrategy<T>
{
    class FindStrategySql
    {
        String     sql;
        MapField[] selectFields;
        MapField[] whereFields;
    }
    
    private Map<String, FindStrategySql> strategyMap = new HashMap<String, FindStrategySql>();
    private final SqlPreInterceptor[]    preInterceptors;
    private final Class<T>               ckass;
    private final ResultSetTransfer<T>   resultSetTransfer;
    
    public FindByStrategyImpl(Class<T> ckass, MapField[] mapFields, SqlPreInterceptor[] preInterceptors)
    {
        resultSetTransfer = new FixBeanTransfer<T>(ckass);
        this.ckass = ckass;
        this.preInterceptors = preInterceptors;
        String tableName = ckass.getAnnotation(TableEntity.class).name();
        Map<String, MapField> map = parse(mapFields);
        if (ckass.isAnnotationPresent(SqlStrategy.class))
        {
            SqlStrategy sqlStrategy = ckass.getAnnotation(SqlStrategy.class);
            if (sqlStrategy.findStrategies().length > 0)
            {
                for (FindStrategy each : sqlStrategy.findStrategies())
                {
                    StringCache cache = new StringCache();
                    List<MapField> selectFields = new LinkedList<MapField>();
                    List<MapField> whereFields = new LinkedList<MapField>();
                    cache.append("select ");
                    for (String selectField : each.selectFields().split(","))
                    {
                        cache.append(selectField).appendComma();
                        selectFields.add(map.get(selectField));
                    }
                    for (String whereField : each.whereFields().split(","))
                    {
                        cache.append(whereField).appendComma();
                        selectFields.add(map.get(whereField));
                    }
                    cache.deleteLast().append(" from ").append(tableName).append(" where ");
                    for (String whereField : each.whereFields().split(","))
                    {
                        cache.append(whereField).append("=? and ");
                        whereFields.add(map.get(whereField));
                    }
                    cache.deleteEnds(4);
                    FindStrategySql findStrategySql = new FindStrategySql();
                    findStrategySql.sql = cache.toString();
                    findStrategySql.selectFields = selectFields.toArray(new MapField[selectFields.size()]);
                    findStrategySql.whereFields = whereFields.toArray(new MapField[whereFields.size()]);
                    strategyMap.put(each.name(), findStrategySql);
                }
            }
            else
            {
                ;
            }
        }
        else
        {
            ;
        }
    }
    
    private Map<String, MapField> parse(MapField[] mapFields)
    {
        Map<String, MapField> map = new HashMap<String, MapField>();
        for (MapField each : mapFields)
        {
            map.put(each.getFieldName(), each);
        }
        return map;
    }
    
    @Override
    public T findOne(Connection connection, T param, String strategyName)
    {
        FindStrategySql findStrategySql = strategyMap.get(strategyName);
        String sql = findStrategySql.sql;
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(sql, param);
        }
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            int index = 1;
            for (MapField each : findStrategySql.whereFields)
            {
                each.setStatementValue(pstat, param, index);
                index += 1;
            }
            ResultSet resultSet = pstat.executeQuery();
            if (resultSet.next())
            {
                T entity = ckass.newInstance();
                for (MapField each : findStrategySql.selectFields)
                {
                    each.setEntityValue(entity, resultSet);
                }
                if (resultSet.next() == false)
                {
                    return entity;
                }
                else
                {
                    throw new IllegalArgumentException("查询结果不止一个数据,异常");
                }
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public List<T> findAll(Connection connection, T param, String strategyName)
    {
        List<T> list = new ArrayList<T>();
        FindStrategySql findStrategySql = strategyMap.get(strategyName);
        String sql = findStrategySql.sql;
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(sql, param);
        }
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            int index = 1;
            for (MapField each : findStrategySql.whereFields)
            {
                each.setStatementValue(pstat, param, index);
                index += 1;
            }
            ResultSet resultSet = pstat.executeQuery();
            while (resultSet.next())
            {
                T entity = ckass.newInstance();
                for (MapField each : findStrategySql.selectFields)
                {
                    each.setEntityValue(entity, resultSet);
                }
                list.add(entity);
            }
            return list;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findPage(Connection connection, T param, String strategyName, Page page, PageParse pageParse)
    {
        FindStrategySql findStrategySql = strategyMap.get(strategyName);
        try
        {
            pageParse.doQuery(param, findStrategySql.whereFields, connection, findStrategySql.sql, resultSetTransfer, page);
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
        }
        return (List<T>) page.getData();
    }
    
}
