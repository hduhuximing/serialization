package com.jfireframework.context.test.runner;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class EachMethodEachContextRunner extends BlockJUnit4ClassRunner
{
    private Class<?> klass;
    private String   path;
    
    public EachMethodEachContextRunner(Class<?> klass) throws InitializationError, URISyntaxException
    {
        super(klass);
        this.klass = klass;
        ConfigPath path = klass.getAnnotation(ConfigPath.class);
        this.path = path.value();
        
    }
    
    @SuppressWarnings("deprecation")
    protected Statement methodBlock(FrameworkMethod method)
    {
        Object test = createTest(method.getMethod());
        Statement statement = methodInvoker(method, test);
        statement = possiblyExpectingExceptions(method, test, statement);
        statement = withPotentialTimeout(method, test, statement);
        statement = withBefores(method, test, statement);
        statement = withAfters(method, test, statement);
        statement = withRules(method, test, statement);
        return statement;
    }
    
    private Statement withRules(FrameworkMethod method, Object target, Statement statement)
    {
        List<TestRule> testRules = getTestRules(target);
        Statement result = statement;
        result = withMethodRules(method, testRules, target, result);
        result = withTestRules(method, testRules, result);
        
        return result;
    }
    
    private Statement withTestRules(FrameworkMethod method, List<TestRule> testRules, Statement statement)
    {
        return testRules.isEmpty() ? statement : new RunRules(statement, testRules, describeChild(method));
    }
    
    private Statement withMethodRules(FrameworkMethod method, List<TestRule> testRules, Object target, Statement result)
    {
        for (org.junit.rules.MethodRule each : getMethodRules(target))
        {
            if (!testRules.contains(each))
            {
                result = each.apply(result, method, target);
            }
        }
        return result;
    }
    
    private List<org.junit.rules.MethodRule> getMethodRules(Object target)
    {
        return rules(target);
    }
    
    protected Object createTest(Method method)
    {
        JfireContext beanContext = new JfireContextImpl();
        File config = null;
        if (path.startsWith("classpath:"))
        {
            try
            {
                config = new File(this.getClass().getClassLoader().getResource(path.substring(10)).toURI());
            }
            catch (Exception e)
            {
                throw new RuntimeException("文件无法找到", e);
            }
        }
        else if (path.startsWith("file:"))
        {
            try
            {
                config = new File(path.substring(5));
            }
            catch (Exception e)
            {
                throw new RuntimeException("文件无法找到", e);
            }
        }
        beanContext.readConfig(config);
        beanContext.addBean(klass.getName(), false, klass);
        if (method.isAnnotationPresent(PropertyAdd.class))
        {
            Properties properties = new Properties();
            PropertyAdd add = method.getAnnotation(PropertyAdd.class);
            for (String each : add.value().split(","))
            {
                String[] tmp = each.split("=");
                properties.put(tmp[0], tmp[1]);
            }
            beanContext.addProperties(properties);
        }
        beanContext.initContext();
        return beanContext.getBean(klass);
    }
}
