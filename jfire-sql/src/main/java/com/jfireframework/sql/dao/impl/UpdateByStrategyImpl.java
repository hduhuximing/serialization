package com.jfireframework.sql.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.annotation.SqlStrategy;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.annotation.UpdateStrategy;
import com.jfireframework.sql.dao.UpdateByStrategy;
import com.jfireframework.sql.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.resultsettransfer.field.MapField;

public class UpdateByStrategyImpl<T> implements UpdateByStrategy<T>
{
    
    class UpdateStrategySql
    {
        String     sql;
        MapField[] fields;
    }
    
    private Map<String, UpdateStrategySql> strategyMap = new HashMap<String, UpdateStrategySql>();
    private final SqlPreInterceptor[]      preInterceptors;
    
    public UpdateByStrategyImpl(Class<T> ckass, MapField[] mapFields, SqlPreInterceptor[] preInterceptors)
    {
        this.preInterceptors = preInterceptors;
        String tableName = ckass.getAnnotation(TableEntity.class).name();
        if (ckass.isAnnotationPresent(SqlStrategy.class))
        {
            SqlStrategy sqlStrategy = ckass.getAnnotation(SqlStrategy.class);
            if (sqlStrategy.updateStrategies().length > 0)
            {
                Map<String, MapField> map = parse(mapFields);
                for (UpdateStrategy each : sqlStrategy.updateStrategies())
                {
                    StringCache cache = new StringCache();
                    List<MapField> fields = new LinkedList<MapField>();
                    cache.append("update ").append(tableName).append(" set ");
                    for (String setField : each.setFields().split(","))
                    {
                        cache.append(map.get(setField).getColName()).append("=?,");
                        fields.add(map.get(setField));
                    }
                    cache.deleteLast().append(" where ");
                    for (String whereField : each.whereFields().split(","))
                    {
                        cache.append(map.get(whereField).getColName()).append("=? and ");
                        fields.add(map.get(whereField));
                    }
                    cache.deleteEnds(4);
                    UpdateStrategySql updateStrategySql = new UpdateStrategySql();
                    updateStrategySql.sql = cache.toString();
                    updateStrategySql.fields = fields.toArray(new MapField[fields.size()]);
                    strategyMap.put(each.name(), updateStrategySql);
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
