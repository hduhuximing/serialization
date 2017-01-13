package com.jfireframework.mvc.core;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView
{
    private Map<String, Object> data = null;
    private String              modelName;
    // 视图的类型
    private String              contentType;
    private Map<String, String> header;
    
    public void setHeader(String key, String value)
    {
        if (header == null)
        {
            header = new HashMap<String, String>();
        }
        header.put(key, value);
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    public ModelAndView(String modelName)
    {
        this.modelName = modelName;
    }
    
    public void addObject(String key, Object value)
    {
        if (data == null)
        {
            data = new HashMap<String, Object>();
        }
        data.put(key, value);
    }
    
    public void setDataMap(Map<String, Object> data)
    {
        this.data = data;
    }
    
    public String getModelName()
    {
        return modelName;
    }
    
    public Map<String, Object> getData()
    {
        return data;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public Map<String, String> getHeader()
    {
        return header;
    }
    
    public void setHeader(Map<String, String> header)
    {
        this.header = header;
    }
    
}
