package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.validation.constraints.Max;

public class MaxValidator extends AbstractConstraintValidator<Max, Number>
{
    private long max;
    
    @Override
    public void initialize(Max c, Field field)
    {
        max = c.value();
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(Number value)
    {
        if (value == null)
        {
            return true;
        }
        else if (value instanceof Double)
        {
            if ((Double) value == Double.NEGATIVE_INFINITY)
            {
                return true;
            }
            else if (Double.isNaN((Double) value) || (Double) value == Double.POSITIVE_INFINITY)
            {
                return false;
            }
        }
        else if (value instanceof Float)
        {
            if ((Float) value == Float.NEGATIVE_INFINITY)
            {
                return true;
            }
            else if (Float.isNaN((Float) value) || (Float) value == Float.POSITIVE_INFINITY)
            {
                return false;
            }
        }
        if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).compareTo(BigDecimal.valueOf(max)) != 1;
        }
        else if (value instanceof BigInteger)
        {
            return ((BigInteger) value).compareTo(BigInteger.valueOf(max)) != 1;
        }
        else
        {
            long longValue = value.longValue();
            return longValue <= max;
        }
    }
    
}
