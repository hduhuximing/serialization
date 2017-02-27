package com.jfireframework.mvc.binder.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class MapBinder implements DataBinder
{
    
    private final String prefixName;
    
    public MapBinder(String prefixName)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueResolver treeValueNode, HttpServletResponse response)
    {
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<String, ParamResolver> entry : treeValueNode.entrySet())
        {
            if (entry.getValue() instanceof StringValueResolver)
            {
                map.put(entry.getKey(), ((StringValueResolver) entry.getValue()).getValue());
            }
        }
        return map;
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
