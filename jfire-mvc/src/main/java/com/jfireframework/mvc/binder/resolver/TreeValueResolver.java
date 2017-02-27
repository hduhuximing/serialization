package com.jfireframework.mvc.binder.resolver;

import java.util.HashMap;

public class TreeValueResolver extends HashMap<String, ParamResolver> implements ParamResolver
{
    /**
     * 
     */
    private static final long serialVersionUID = -7949905578641740166L;
    
    public TreeValueResolver()
    {
    }
    
    public TreeValueResolver(String text, String value)
    {
        put(text, value);
    }
    
    public void put(String text, String value)
    {
        if (text.charAt(0) == '[')
        {
            int end = text.indexOf(']');
            String key = text.substring(1, end);
            if (end == text.length() - 1)
            {
                if (key.length() == 0)
                {
                    // 如果key的长度是2，意味着原始内容实际上是[]，那么就是说这是个数组
                    put(String.valueOf(size()), new StringValueResolver(value));
                }
                else
                {
                    if (containsKey(key))
                    {
                        ArrayResolver arrayNode = new ArrayResolver();
                        StringValueResolver node = (StringValueResolver) get(key);
                        arrayNode.add(node.getValue());
                        arrayNode.add(value);
                        put(key, arrayNode);
                    }
                    else
                    {
                        put(key, new StringValueResolver(value));
                    }
                }
            }
            else
            {
                String nestedText = text.substring(end + 1);
                if (nestedText.length() == 0)
                {
                    
                }
                else
                {
                    if (containsKey(key))
                    {
                        TreeValueResolver node = (TreeValueResolver) get(key);
                        node.put(nestedText, value);
                    }
                    else
                    {
                        put(key, new TreeValueResolver(nestedText, value));
                    }
                }
            }
        }
        else
        {
            int index = 0;
            if ((index = text.indexOf('[')) > 0)
            {
                String keyName = text.substring(0, index);
                String nestedText = text.substring(index);
                if (containsKey(keyName) == false)
                {
                    put(keyName, new TreeValueResolver(nestedText, value));
                }
                else
                {
                    TreeValueResolver node = (TreeValueResolver) get(keyName);
                    node.put(nestedText, value);
                }
            }
            else
            {
                if (containsKey(text))
                {
                    ParamResolver node = get(text);
                    if (node instanceof StringValueResolver)
                    {
                        ArrayResolver arrayNode = new ArrayResolver();
                        arrayNode.add(((StringValueResolver) node).getValue());
                        arrayNode.add(value);
                        put(text, arrayNode);
                    }
                    else if (node instanceof ArrayResolver)
                    {
                        ((ArrayResolver) node).add(value);
                    }
                }
                else
                {
                    put(text, new StringValueResolver(value));
                }
            }
        }
        
    }
    
}
