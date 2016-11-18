package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.Length;

public class LengthValidator extends AbstractConstraintValidator<Length, String>
{
    
    private int length;
    
    @Override
    public void initialize(Length c, Field field)
    {
        length = c.value();
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(String value)
    {
        if (value == null || value.length() <= length)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
