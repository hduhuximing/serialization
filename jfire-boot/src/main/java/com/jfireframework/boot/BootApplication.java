package com.jfireframework.boot;

import java.util.Properties;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class BootApplication
{
    private String configClassName;
    private String packageName;
    
    private void init(String configClassName)
    {
        this.configClassName = configClassName;
        int index = configClassName.lastIndexOf('.');
        if (index != -1)
        {
            packageName = configClassName.substring(0, index);
        }
    }
    
    public BootApplication()
    {
        configClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        init(configClassName);
    }
    
    public BootApplication(Class<?> ckass)
    {
        init(ckass.getName());
    }
    
    public void start()
    {
        try
        {
            Class<?> ckass = Class.forName(configClassName);
            if (ckass.isAnnotationPresent(AppInfo.class) == false)
            {
                throw new NullPointerException("无法在类:" + configClassName + "上找到AppInfo注解,无法获取需要启动的信息");
            }
            else
            {
                JfireContext jfireContext = new JfireContextImpl();
                jfireContext.readConfig(ckass);
                jfireContext.addPackageNames(packageName);
                AppInfo appInfo = ckass.getAnnotation(AppInfo.class);
                Properties properties = new Properties();
                properties.put("boot_appName", appInfo.appName());
                properties.put("boot_port", String.valueOf(appInfo.port()));
                properties.put("boot_configClassName", configClassName);
                properties.put("boot_packageName", packageName);
                properties.put("jfire.mvc.classpathPrefix", appInfo.prefix());
                properties.put("jfire.mvc.mode", "run_in_jar_mode");
                jfireContext.addProperties(properties);
                jfireContext.addBean("bootStarter", false, BootStarter.class);
                BootStarter bootStarter = (BootStarter) jfireContext.getBean("bootStarter");
                bootStarter.start();
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new JustThrowException(e);
        }
        
    }
}
