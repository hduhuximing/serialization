package com.jfireframework.sql.dao;

import java.sql.Connection;
import java.util.List;
import com.jfireframework.sql.annotation.SqlStrategy;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.page.PageParse;

public interface StrategyOperation<T>
{
    public int update(Connection connection, T param, String strategyName);
    
    public T findOne(Connection connection, T entity, String strategyName);
    
    public List<T> findAll(Connection connection, T param, String strategyName);
    
    public List<T> findPage(Connection connection, T param, Page page, PageParse pageParse, String strategyName);
}
