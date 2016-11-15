package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.extra.dbstructure.ColNameStrategy;

public class WLongField extends AbstractMapField
{
    
    public WLongField(Field field, ColNameStrategy colNameStrategy)
    {
        super(field, colNameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        long value = resultSet.getLong(dbColName);
        if (resultSet.wasNull())
        {
            unsafe.putObject(entity, offset, null);
        }
        else
        {
            unsafe.putObject(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        Long value = (Long) unsafe.getObject(entity, offset);
        if (value == null)
        {
            statement.setObject(index, null);
        }
        else
        {
            statement.setLong(index, value);
        }
    }
    
}
