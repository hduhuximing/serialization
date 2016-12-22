package com.jfireframework.sql.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.resultsettransfer.ResultSetTransfer;
import com.jfireframework.sql.resultsettransfer.field.MapField;

public class OracleParse implements PageParse
{
    private String parseQuerySql(String originSql)
    {
        String querySql = PageSqlCache.getQuerySql(originSql);
        if (querySql == null)
        {
            querySql = "select * from ( select a.*,rownum from(" + originSql + ") a where rownum<=?) where rownum>=?";
            PageSqlCache.putQuerySql(originSql, querySql);
        }
        return querySql;
    }
    
    private String parseCountSql(String originSql)
    {
        String countSql = PageSqlCache.getCountSql(originSql);
        if (countSql == null)
        {
            int index = originSql.indexOf("from");
            countSql = "select count(*) " + originSql.substring(index);
            PageSqlCache.putCountSql(originSql, countSql);
        }
        return countSql;
    }
    
    @Override
    public void doQuery(Object[] params, Connection connection, String sql, ResultSetTransfer<?> transfer, Page page) throws SQLException
    {
        PreparedStatement pstat = null;
        ResultSet resultSet = null;
        try
        {
            String querySql = parseQuerySql(sql);
            String countSql = parseCountSql(sql);
            pstat = connection.prepareStatement(querySql);
            int index = 1;
            for (Object param : params)
            {
                pstat.setObject(index++, param);
            }
            pstat.setInt(index++, page.getStart() + page.getPageSize());
            pstat.setInt(index, page.getStart());
            resultSet = pstat.executeQuery();
            List<?> list = transfer.transferList(resultSet, querySql);
            page.setData(list);
            resultSet.close();
            pstat.close();
            pstat = connection.prepareStatement(countSql);
            index = 1;
            for (Object param : params)
            {
                pstat.setObject(index++, param);
            }
            resultSet = pstat.executeQuery();
            resultSet.next();
            page.setTotal(resultSet.getInt(1));
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (pstat != null)
                {
                    pstat.close();
                }
            }
            catch (SQLException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    @Override
    public void doQuery(Object entity, MapField[] fields, Connection connection, String sql, ResultSetTransfer<?> transfer, Page page) throws SQLException
    {
        PreparedStatement pstat = null;
        ResultSet resultSet = null;
        try
        {
            String querySql = parseQuerySql(sql);
            String countSql = parseCountSql(sql);
            pstat = connection.prepareStatement(querySql);
            int index = 1;
            for (MapField each : fields)
            {
                each.setStatementValue(pstat, entity, index);
                index += 1;
            }
            pstat.setInt(index++, page.getStart() + page.getPageSize());
            pstat.setInt(index, page.getStart());
            resultSet = pstat.executeQuery();
            List<?> list = transfer.transferList(resultSet, querySql);
            page.setData(list);
            resultSet.close();
            pstat.close();
            pstat = connection.prepareStatement(countSql);
            index = 1;
            for (MapField each : fields)
            {
                each.setStatementValue(pstat, entity, index);
                index += 1;
            }
            resultSet = pstat.executeQuery();
            resultSet.next();
            page.setTotal(resultSet.getInt(1));
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (pstat != null)
                {
                    pstat.close();
                }
            }
            catch (SQLException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
}
