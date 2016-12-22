package com.jfireframework.sql.dao;

import java.sql.Connection;
import java.util.List;
import com.jfireframework.sql.resultsettransfer.field.MapField;

public interface Dao<T> extends FindStrategy<T>, UpdateStrategy<T>
{
    
    /**
     * 将对象信息保存到数据库中。如果对象id值为null，进行插入操作，否则进行更新操作
     * 
     * 
     * @param entity
     * @param connection
     * @return
     */
    public void save(T entity, Connection connection);
    
    /**
     * 将一个对象以插入数据的形式保存到数据库
     * 
     * @param
     * 
     * @param entity
     * @param connection
     */
    public void insert(T entity, Connection connection);
    
    public int update(T entity, Connection connection);
    
    /**
     * 批量将一个list中的数据保存到数据库中
     * 
     * 
     * @param entitys
     * @param connection
     */
    public void batchInsert(List<T> entitys, Connection connection);
    
    /**
     * 将对象entity所代表的数据库行删除. entity其他参数并不重要,只要id参数有存在即可.删除是根据id参数进行删除的
     * 
     * @param pk
     * @param connection
     * @return
     */
    public int delete(T entity, Connection connection);
    
    /**
     * 在数据库该表中，使用主键查询并且返回对象
     * 
     * @param
     * 
     * @param pk
     * @param connection
     * @return
     */
    public T getById(Object pk, Connection connection);
    
    /**
     * 在数据表该表中，使用主键查询并且返回对象，但是使用某一个锁定模式
     * 
     * @param pk
     * @param connection
     * @param mode
     * @return
     */
    public T getById(Object pk, Connection connection, LockMode mode);
    
    public int deleteAll(Connection connection);
    
}
