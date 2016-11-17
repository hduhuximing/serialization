package com.jfireframework.beanvalidation;

import com.jfireframework.beanvalidation.constraint.Length;
import com.jfireframework.beanvalidation.constraint.Max;

public class Data
{
    @Max(28)
    private int    age;
    @Length(7)
    private String name;
    
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
}
