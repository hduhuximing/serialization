package com.jfireframework.sql.dao.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.FindByStrategy;
import com.jfireframework.sql.annotation.StrategyBind;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.dao.FindStrategy;
import com.jfireframework.sql.dbstructure.ColNameStrategy;
import com.jfireframework.sql.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.page.PageParse;
import com.jfireframework.sql.resultsettransfer.FixBeanTransfer;
import com.jfireframework.sql.resultsettransfer.ResultSetTransfer;
import com.jfireframework.sql.resultsettransfer.field.MapField;
import com.jfireframework.sql.resultsettransfer.field.MapFieldBuilder;

public class FindStrategyImpl<T> implements FindStrategy<T>
{
    class FindStrategySql
    {
        String     sql;
        MapField[] selectFields;
        MapField[] whereFields;
    }
    
    private Map<String, FindStrategySql> strategyMap = new HashMap<String, FindStrategyImpl<T>.FindStrategySql>();
    private final SqlPreInterceptor[]    preInterceptors;
    private final Class<T>               ckass;
    private final ResultSetTransfer<T>   resultSetTransfer;
    
    public FindStrategyImpl(Class<T> ckass, ColNameStrategy colNameStrategy, SqlPreInterceptor[] preInterceptors)
    {
        resultSetTransfer = new FixBeanTransfer<T>(ckass);
        this.ckass = ckass;
        this.preInterceptors = preInterceptors;
        String tableName = ckass.getAnnotation(TableEntity.class).name();
        Map<String, Set<Field>> fieldStrategyMap = new HashMap<String, Set<Field>>();
        Map<String, Set<Field>> findStrategyMap = new HashMap<String, Set<Field>>();
        Field[] fields = ReflectUtil.getAllFields(ckass);
        for (Field each : fields)
        {
            if (each.isAnnotationPresent(StrategyBind.class))
            {
                StrategyBind bind = each.getAnnotation(StrategyBind.class);
                String[] tmp = bind.value().split(",");
                for (String name : tmp)
                {
                    addFieldStrategy(name, each, fieldStrategyMap);
                }
            }
            if (each.isAnnotationPresent(FindByStrategy.class))
            {
                FindByStrategy findByStrategy = each.getAnnotation(FindByStrategy.class);
                String[] tmp = findByStrategy.value().split(",");
                for (String name : tmp)
                {
                    addFindStrategy(name, each, findStrategyMap);
                    addFieldStrategy(name, each, fieldStrategyMap);
                }
            }
        }
        for (Entry<String, Set<Field>> entry : findStrategyMap.entrySet())
        {
            StringCache cache = new StringCache();
            List<MapField> selectFields = new LinkedList<MapField>();
            if (fieldStrategyMap.containsKey(entry.getKey()))
            {
                for (Field each : fieldStrategyMap.get(entry.getKey()))
                {
                    selectFields.add(MapFieldBuilder.buildMapField(each, colNameStrategy));
                }
            }
            else
            {
                for (Field each : fields)
                {
                    selectFields.add(MapFieldBuilder.buildMapField(each, colNameStrategy));
                }
            }
            cache.append("select ");
            for (MapField each : selectFields)
            {
                cache.append(each.getColName()).appendComma();
            }
            if (cache.isCommaLast())
            {
                cache.deleteLast();
            }
            cache.append(" from ").append(tableName).append(" where ");
            List<MapField> whereFields = new LinkedList<MapField>();
            for (Field each : entry.getValue())
            {
                whereFields.add(MapFieldBuilder.buildMapField(each, colNameStrategy));
            }
            for (MapField each : whereFields)
            {
                cache.append(each.getColName()).append("=? and ");
            }
            cache.deleteEnds(4);
            FindStrategySql findStrategySql = new FindStrategySql();
            findStrategySql.sql = cache.toString();
            findStrategySql.selectFields = selectFields.toArray(new MapField[selectFields.size()]);
            findStrategySql.whereFields = whereFields.toArray(new MapField[whereFields.size()]);
            strategyMap.put(entry.getKey(), findStrategySql);
        }
    }
    
    private void addFieldStrategy(String strategyName, Field field, Map<String, Set<Field>> fieldStrategyMap)
    {
        Set<Field> list = fieldStrategyMap.get(strategyName);
        if (list == null)
        {
            list = new HashSet<Field>();
            fieldStrategyMap.put(strategyName, list);
        }
        list.add(field);
    }
    
    private void addFindStrategy(String strategyName, Field field, Map<String, Set<Field>> fieldStrategyMap)
    {
        Set<Field> list = fieldStrategyMap.get(strategyName);
        if (list == null)
        {
            list = new HashSet<Field>();
            fieldStrategyMap.put(strategyName, list);
        }
        list.add(field);
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
