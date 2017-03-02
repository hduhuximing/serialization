package com.jfireframework.boot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.jfire.Jfire;
import com.jfireframework.jfire.JfireConfig;

public class BootApplication
{
    private Properties outConfigProperties = new Properties();
    private Class<?>   configClass;
    
    private void init(Class<?> configClass)
    {
        this.configClass = configClass;
        if (new File("boot.properties").exists())
        {
            outConfigProperties = new Properties();
            try (FileInputStream inputStream = new FileInputStream(new File("boot.properties")))
            {
                outConfigProperties.load(inputStream);
            }
            catch (IOException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    public BootApplication(Class<?> configClass)
    {
        init(configClass);
    }
    
    public void start()
    {
        if (configClass.isAnnotationPresent(AppInfo.class) == false)
        {
            throw new NullPointerException("无法在类:" + configClass.getName() + "上找到AppInfo注解,无法获取需要启动的信息");
        }
        else
        {
            JfireConfig jfireConfig = new JfireConfig();
            jfireConfig.readConfig(configClass);
            AppInfo appInfo = configClass.getAnnotation(AppInfo.class);
            Properties properties = new Properties();
            properties.put("boot_appName", appInfo.appName());
            properties.put("boot_port", String.valueOf(appInfo.port()));
            properties.put("boot_configClassName", configClass.getName());
            properties.put("jfire.mvc.classpathPrefix", appInfo.prefix());
            properties.put("jfire.mvc.mode", "run_in_jar_mode");
            properties.putAll(outConfigProperties);
            jfireConfig.addProperties(properties);
            jfireConfig.addBean("bootStarter", false, BootStarter.class);
            Jfire jfire = new Jfire(jfireConfig);
            BootStarter bootStarter = (BootStarter) jfire.getBean("bootStarter");
            bootStarter.start();
        }
    }
}
