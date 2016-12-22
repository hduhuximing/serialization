package com.jfireframework.sql.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import com.jfireframework.sql.annotation.SqlIgnore;
import com.jfireframework.sql.annotation.StrategyBind;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.annotation.UpdateByStrategy;
import com.jfireframework.sql.dao.UpdateStrategy;
import com.jfireframework.sql.dbstructure.ColNameStrategy;
import com.jfireframework.sql.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.resultsettransfer.field.MapField;
import com.jfireframework.sql.resultsettransfer.field.MapFieldBuilder;

public class UpdateStrategyImpl<T> implements UpdateStrategy<T>
{
    
    class UpdateStrategySql
    {
        String     sql;
        MapField[] fields;
    }
    
    private Map<String, UpdateStrategySql> strategyMap = new HashMap<String, UpdateStrategySql>();
    private final SqlPreInterceptor[]      preInterceptors;
    
    public UpdateStrategyImpl(Class<T> ckass, ColNameStrategy colNameStrategy, SqlPreInterceptor[] preInterceptors)
    {
        this.preInterceptors = preInterceptors;
        String tableName = ckass.getAnnotation(TableEntity.class).name();
        Map<String, Set<Field>> fieldStrategyMap = new HashMap<String, Set<Field>>();
        Map<String, Set<Field>> updateStrategyMap = new HashMap<String, Set<Field>>();
        Field[] fields = ReflectUtil.getAllFields(ckass);
        for (Field each : fields)
        {
            if (
                each.isAnnotationPresent(SqlIgnore.class) //
                        || Map.class.isAssignableFrom(each.getType())//
                        || List.class.isAssignableFrom(each.getType())//
                        || each.getType().isInterface()//
                        || Modifier.isStatic(each.getModifiers())
            )
            {
                continue;
            }
            if (each.isAnnotationPresent(StrategyBind.class))
            {
                StrategyBind bind = each.getAnnotation(StrategyBind.class);
                String[] tmp = bind.value().split(",");
                for (String name : tmp)
                {
                    addFieldStrategy(name, each, fieldStrategyMap);
                }
            }
            if (each.isAnnotationPresent(UpdateByStrategy.class))
            {
                UpdateByStrategy updateByStrategy = each.getAnnotation(UpdateByStrategy.class);
                String[] tmp = updateByStrategy.value().split(",");
                for (String name : tmp)
                {
                    addFieldStrategy(name, each, fieldStrategyMap);
                    addUpdateStrategy(name, each, updateStrategyMap);
                }
            }
        }
        for (Entry<String, Set<Field>> entry : updateStrategyMap.entrySet())
        {
            if (fieldStrategyMap.containsKey(entry.getKey()))
            {
                StringCache cache = new StringCache();
                cache.append("update ").append(tableName).append(" set ");
                List<MapField> tmp = new LinkedList<MapField>();
                for (Field each : fieldStrategyMap.get(entry.getKey()))
                {
                    MapField mapField = MapFieldBuilder.buildMapField(each, colNameStrategy);
                    cache.append(mapField.getColName()).append("=?,");
                    tmp.add(mapField);
                }
                if (cache.isCommaLast())
                {
                    cache.deleteLast();
                }
                cache.append(" where ");
                for (Field each : entry.getValue())
                {
                    MapField mapField = MapFieldBuilder.buildMapField(each, colNameStrategy);
                    cache.append(mapField.getColName()).append("=? and ");
                    tmp.add(mapField);
                }
                cache.deleteEnds(4);
                UpdateStrategySql updateStrategySql = new UpdateStrategySql();
                updateStrategySql.sql = cache.toString();
                updateStrategySql.fields = tmp.toArray(new MapField[tmp.size()]);
                strategyMap.put(entry.getKey(), updateStrategySql);
            }
            else
            {
                throw new UnsupportedOperationException("策略:" + entry.getKey() + "没有对应的属性可以更新,请检查" + ckass.getName());
            }
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
    
    private void addUpdateStrategy(String strategyName, Field field, Map<String, Set<Field>> fieldStrategyMap)
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
    public int update(T param, Connection connection, String strategyName)
    {
        UpdateStrategySql updateStrategySql = strategyMap.get(strategyName);
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
