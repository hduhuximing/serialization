package com.jfireframework.context.config;

import java.util.HashMap;

public class Profile
{
    private String                  name;
    private String[]                packageNames  = new String[0];
    private BeanInfo[]              beans         = new BeanInfo[0];
    private String[]                propertyPaths = new String[0];
    private HashMap<String, String> properties    = new HashMap<String, String>();
    
    public HashMap<String, String> getProperties()
    {
        return properties;
    }
    
    public void setProperties(HashMap<String, String> properties)
    {
        this.properties = properties;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String[] getPackageNames()
    {
        return packageNames;
    }
    
    public void setPackageNames(String[] packageNames)
    {
        this.packageNames = packageNames;
    }
    
    public BeanInfo[] getBeans()
    {
        return beans;
    }
    
    public void setBeans(BeanInfo[] beans)
    {
        this.beans = beans;
    }
    
    public String[] getPropertyPaths()
    {
        return propertyPaths;
    }
    
    public void setPropertyPaths(String[] propertyPaths)
    {
        this.propertyPaths = propertyPaths;
    }
    
}
