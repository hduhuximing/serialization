package com.jfireframework.sql.dao;

import java.sql.Connection;

public interface UpdateStrategy<T>
{
    public int update(T param, Connection connection, String strategyName);
}
