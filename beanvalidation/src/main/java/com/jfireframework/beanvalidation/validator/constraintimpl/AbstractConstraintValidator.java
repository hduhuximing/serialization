package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Properties;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public abstract class AbstractConstraintValidator<C extends Annotation, V> implements ConstraintValidator<C, V>
{
    protected static final Properties properties;
    static
    {
        properties = new Properties();
        InputStream inputStream = AbstractConstraintValidator.class.getClassLoader().getResourceAsStream("valid.properties");
        if (inputStream != null)
        {
            try
            {
                properties.load(inputStream);
            }
            catch (IOException e)
            {
            }
            finally
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected String getMessage(String value)
    {
        if (value.charAt(0) == '{')
        {
            value = value.substring(1, value.length() - 1);
            value = properties.getProperty(value);
            return value;
        }
        else
        {
            return value;
        }
    }
}
