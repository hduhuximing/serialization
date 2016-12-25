package com.jfireframework.mvc.util;

import javax.annotation.Resource;
import com.jfireframework.context.bean.annotation.field.PropertyRead;

@Resource
public class ExtraConfig
{
    @PropertyRead("encode")
    private String  encode  = "UTF-8";
    @PropertyRead("hotswap")
    private boolean hotswap = false;
    @PropertyRead("monitorPath")
    private String  monitorPath;
    @PropertyRead("reloadPath")
    private String  reloadPath;
    @PropertyRead("reloadPackages")
    private String  reloadPackages;
    @PropertyRead("excludePackages")
    private String  excludePackages;
    
    public String getExcludePackages()
    {
        return excludePackages;
    }
    
    public void setExcludePackages(String excludePackages)
    {
        this.excludePackages = excludePackages;
    }
    
    public String getEncode()
    {
        return encode;
    }
    
    public void setEncode(String encode)
    {
        this.encode = encode;
    }
    
    public boolean isHotswap()
    {
        return hotswap;
    }
    
    public void setHotswap(boolean hotswap)
    {
        this.hotswap = hotswap;
    }
    
    public String getMonitorPath()
    {
        return monitorPath;
    }
    
    public void setMonitorPath(String monitorPath)
    {
        this.monitorPath = monitorPath;
    }
    
    public String getReloadPath()
    {
        return reloadPath;
    }
    
    public void setReloadPath(String reloadPath)
    {
        this.reloadPath = reloadPath;
    }
    
    public String getReloadPackages()
    {
        return reloadPackages;
    }
    
    public void setReloadPackages(String reloadPackages)
    {
        this.reloadPackages = reloadPackages;
    }
    
}
