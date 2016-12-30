package com.jfireframework.mvc.binder.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public class MapBinder implements DataBinder
{
    
    private final String prefixName;
    
    public MapBinder(String prefixName)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<String, ParamNode> entry : treeValueNode.entrySet())
        {
            if (entry.getValue() instanceof StringValueNode)
            {
                map.put(entry.getKey(), ((StringValueNode) entry.getValue()).getValue());
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
