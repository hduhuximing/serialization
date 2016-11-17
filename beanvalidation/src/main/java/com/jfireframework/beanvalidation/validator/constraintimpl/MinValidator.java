package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.Min;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class MinValidator implements ConstraintValidator<Min, Number>
{
    private long min;
    
    @Override
    public String message()
    {
        return "数值的最小值不能低于" + min;
    }
    
    @Override
    public void initialize(Min c, Field field)
    {
        min = c.value();
    }
    
    @Override
    public boolean isValid(Number value)
    {
        if (value == null || value.longValue() < min)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
}
