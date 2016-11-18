package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.validation.constraints.Min;

public class MinValidator extends AbstractConstraintValidator<Min, Number>
{
    private long min;
    
    @Override
    public void initialize(Min c, Field field)
    {
        min = c.value();
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(Number value)
    {
        // null values are valid
        if (value == null)
        {
            return true;
        }
        // handling of NaN, positive infinity and negative infinity
        else if (value instanceof Double)
        {
            if ((Double) value == Double.POSITIVE_INFINITY)
            {
                return true;
            }
            else if (Double.isNaN((Double) value) || (Double) value == Double.NEGATIVE_INFINITY)
            {
                return false;
            }
        }
        else if (value instanceof Float)
        {
            if ((Float) value == Float.POSITIVE_INFINITY)
            {
                return true;
            }
            else if (Float.isNaN((Float) value) || (Float) value == Float.NEGATIVE_INFINITY)
            {
                return false;
            }
        }
        
        if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).compareTo(BigDecimal.valueOf(min)) != -1;
        }
        else if (value instanceof BigInteger)
        {
            return ((BigInteger) value).compareTo(BigInteger.valueOf(min)) != -1;
        }
        else
        {
            long longValue = value.longValue();
            return longValue >= min;
        }
    }
    
}
