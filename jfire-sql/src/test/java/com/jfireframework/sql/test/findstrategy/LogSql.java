package com.jfireframework.sql.test.findstrategy;

import com.jfireframework.sql.interceptor.SqlPreInterceptor;

public class LogSql implements SqlPreInterceptor
{
    
    @Override
    public String preIntercept(String sql, Object... params)
    {
        System.out.println(sql);
        return sql;
    }
    
}
