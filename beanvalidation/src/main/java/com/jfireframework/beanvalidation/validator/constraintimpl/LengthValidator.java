package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.Length;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class LengthValidator implements ConstraintValidator<Length, String>
{
    
    private int length;
    
    @Override
    public String message()
    {
        return "字符串长度必须在指定的范围内";
    }
    
    @Override
    public void initialize(Length c, Field field)
    {
        length = c.value();
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
