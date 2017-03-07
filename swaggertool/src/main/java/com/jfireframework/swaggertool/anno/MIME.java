package com.jfireframework.swaggertool.anno;

public enum MIME
{
    json("application/json"), //
    formUrlencoded("application/x-www-form-urlencoded"), //
    stream("application/octet-stream"), //
    text("text/html");
    private final String type;
    
    private MIME(String type)
    {
        this.type = type;
    }
    
    public String type()
    {
        return type;
    }
}
