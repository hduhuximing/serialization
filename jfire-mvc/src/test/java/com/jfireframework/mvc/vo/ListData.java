package com.jfireframework.mvc.vo;

import java.util.List;

public class ListData
{
    private List<Desk>   desks;
    private List<String> names;
    
    public List<String> getNames()
    {
        return names;
    }
    
    public void setNames(List<String> names)
    {
        this.names = names;
    }
    
    public List<Desk> getDesks()
    {
        return desks;
    }
    
    public void setDesks(List<Desk> desks)
    {
        this.desks = desks;
    }
    
}
