package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.jfireframework.sql.extra.dbstructure.ColNameStrategy;

public class TimestampField extends AbstractMapField
{
    
    public TimestampField(Field field, ColNameStrategy colNameStrategy)
    {
        super(field, colNameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        unsafe.putObject(entity, offset, resultSet.getTimestamp(dbColName));
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setTimestamp(index, (Timestamp) unsafe.getObject(entity, offset));
    }
    
}
