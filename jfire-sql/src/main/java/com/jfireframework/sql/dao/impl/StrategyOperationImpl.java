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
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.annotation.SqlStrategy;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.dao.StrategyOperation;
import com.jfireframework.sql.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.page.PageParse;
import com.jfireframework.sql.resultsettransfer.FixBeanTransfer;
import com.jfireframework.sql.resultsettransfer.ResultSetTransfer;
import com.jfireframework.sql.resultsettransfer.field.MapField;

public class StrategyOperationImpl<T> implements StrategyOperation<T>
{
    class FindStrategySql
    {
        String     sql;
        MapField[] selectFields;
        MapField[] whereFields;
    }
    
    class UpdateStrategySql
    {
        String     sql;
        MapField[] fields;
    }
    
    private final SqlPreInterceptor[]                               preInterceptors;
    private final Class<T>                                          ckass;
    private final ResultSetTransfer<T>                              resultSetTransfer;
    private final Map<String, MapField>                             mapFields;
    private final ConcurrentHashMap<SqlStrategy, FindStrategySql>   findMap   = new ConcurrentHashMap<SqlStrategy, FindStrategySql>();
    private final ConcurrentHashMap<SqlStrategy, UpdateStrategySql> updateMap = new ConcurrentHashMap<SqlStrategy, UpdateStrategySql>();
    private final String                                            tableName;
    
    public StrategyOperationImpl(Class<T> ckass, MapField[] mapFields, SqlPreInterceptor[] preInterceptors)
    {
        resultSetTransfer = new FixBeanTransfer<T>(ckass);
        this.ckass = ckass;
        this.preInterceptors = preInterceptors;
        this.mapFields = parse(mapFields);
        tableName = ckass.getAnnotation(TableEntity.class).name();
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
    
    private FindStrategySql getFind(SqlStrategy findStrategy)
    {
        FindStrategySql sql = findMap.get(findStrategy);
        if (sql == null)
        {
            sql = buildFind(findStrategy);
            FindStrategySql pre = findMap.putIfAbsent(findStrategy, sql);
            if (pre != null)
            {
                sql = pre;
            }
        }
        return sql;
    }
    
    private UpdateStrategySql getUpdate(SqlStrategy updateStrategy)
    {
        UpdateStrategySql sql = updateMap.get(updateStrategy);
        if (sql == null)
        {
            sql = buildUpdate(updateStrategy);
            UpdateStrategySql pre = updateMap.putIfAbsent(updateStrategy, sql);
            if (pre != null)
            {
                sql = pre;
            }
        }
        return sql;
    }
    
    private FindStrategySql buildFind(SqlStrategy findStrategy)
    {
        StringCache cache = new StringCache();
        List<MapField> selectFields = new LinkedList<MapField>();
        List<MapField> whereFields = new LinkedList<MapField>();
        cache.append("select ");
        for (String selectField : findStrategy.valueFields().split(","))
        {
            cache.append(mapFields.get(selectField).getColName()).appendComma();
            selectFields.add(mapFields.get(selectField));
        }
        for (String whereField : findStrategy.whereFields().split(","))
        {
            cache.append(mapFields.get(whereField).getColName()).appendComma();
            selectFields.add(mapFields.get(whereField));
        }
        cache.deleteLast().append(" from ").append(tableName).append(" where ");
        for (String whereField : findStrategy.whereFields().split(","))
        {
            cache.append(mapFields.get(whereField).getColName()).append("=? and ");
            whereFields.add(mapFields.get(whereField));
        }
        cache.deleteEnds(4);
        FindStrategySql findStrategySql = new FindStrategySql();
        findStrategySql.sql = cache.toString();
        findStrategySql.selectFields = selectFields.toArray(new MapField[selectFields.size()]);
        findStrategySql.whereFields = whereFields.toArray(new MapField[whereFields.size()]);
        return findStrategySql;
    }
    
    private UpdateStrategySql buildUpdate(SqlStrategy sqlStrategy)
    {
        StringCache cache = new StringCache();
        List<MapField> fields = new LinkedList<MapField>();
        cache.append("update ").append(tableName).append(" set ");
        for (String setField : sqlStrategy.valueFields().split(","))
        {
            cache.append(mapFields.get(setField).getColName()).append("=?,");
            fields.add(mapFields.get(setField));
        }
        cache.deleteLast().append(" where ");
        for (String whereField : sqlStrategy.whereFields().split(","))
        {
            cache.append(mapFields.get(whereField).getColName()).append("=? and ");
            fields.add(mapFields.get(whereField));
        }
        cache.deleteEnds(4);
        UpdateStrategySql updateStrategySql = new UpdateStrategySql();
        updateStrategySql.sql = cache.toString();
        updateStrategySql.fields = fields.toArray(new MapField[fields.size()]);
        return updateStrategySql;
    }
    
    @Override
    public T findOne(Connection connection, T param, SqlStrategy findStrategy)
    {
        FindStrategySql findStrategySql = getFind(findStrategy);
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
    public List<T> findAll(Connection connection, T param, SqlStrategy findStrategy)
    {
        List<T> list = new ArrayList<T>();
        FindStrategySql findStrategySql = getFind(findStrategy);
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
    public List<T> findPage(Connection connection, T param, Page page, PageParse pageParse, SqlStrategy findStrategy)
    {
        FindStrategySql findStrategySql = getFind(findStrategy);
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
    
    @Override
    public int update(Connection connection, T param, SqlStrategy updateStrategy)
    {
        UpdateStrategySql updateStrategySql = getUpdate(updateStrategy);
        String sql = updateStrategySql.sql;
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(sql, param);
        }
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            int index = 1;
            for (MapField each : updateStrategySql.fields)
            {
                each.setStatementValue(pstat, param, index);
                index += 1;
            }
            return pstat.executeUpdate();
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
    
}
