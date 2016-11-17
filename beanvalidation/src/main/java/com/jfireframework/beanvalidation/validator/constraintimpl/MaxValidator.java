package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.Max;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class MaxValidator implements ConstraintValidator<Max, Number>
{
    private long max;
    
    @Override
    public String message()
    {
        return "数值的最大不能超过" + max;
    }
    
    @Override
    public void initialize(Max c, Field field)
    {
        max = c.value();
    }
    
    @Override
    public boolean isValid(Number value)
    {
        if (value == null || value.longValue() > max)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
}
