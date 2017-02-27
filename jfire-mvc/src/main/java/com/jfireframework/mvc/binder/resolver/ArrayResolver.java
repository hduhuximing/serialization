package com.jfireframework.mvc.binder.resolver;

import java.util.ArrayList;

public class ArrayResolver implements ParamResolver
{
    private ArrayList<String> array = new ArrayList<String>();
    
    public void add(String value)
    {
        array.add(value);
    }
    
    public ArrayList<String> getArray()
    {
        return array;
    }
    
}
