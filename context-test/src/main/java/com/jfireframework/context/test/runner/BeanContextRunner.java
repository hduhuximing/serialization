package com.jfireframework.context.test.runner;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class BeanContextRunner extends BlockJUnit4ClassRunner
{
    private Class<?>     klass;
    private JfireContext beanContext;
    
    public BeanContextRunner(Class<?> klass) throws InitializationError, URISyntaxException
    {
        super(klass);
        this.klass = klass;
        ConfigPath path = klass.getAnnotation(ConfigPath.class);
        String value = path.value();
        beanContext = new JfireContextImpl();
        File config = null;
        if (value.startsWith("classpath:"))
        {
            try
            {
                config = new File(this.getClass().getClassLoader().getResource(value.substring(10)).toURI());
            }
            catch (Exception e)
            {
                throw new RuntimeException("文件无法找到", e);
            }
        }
        else if (value.startsWith("file:"))
        {
            try
            {
                config = new File(value.substring(5));
            }
            catch (Exception e)
            {
                throw new RuntimeException("文件无法找到", e);
            }
        }
        beanContext.readConfig(config);
        beanContext.addBean(klass.getName(), false, klass);
        if (klass.isAnnotationPresent(PropertyAdd.class))
        {
            Properties properties = new Properties();
            PropertyAdd add = klass.getAnnotation(PropertyAdd.class);
            for (String each : add.value().split(","))
            {
                String[] tmp = each.split("=");
                properties.put(tmp[0], tmp[1]);
            }
            beanContext.addProperties(properties);
        }
        beanContext.initContext();
    }
    
    protected Object createTest()
    {
        return beanContext.getBean(klass);
    }
}
