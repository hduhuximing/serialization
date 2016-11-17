package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.Size;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class SizeValidator implements ConstraintValidator<Size, Number>
{
    private String msg;
    private long   min;
    private long   max;
    
    @Override
    public String message()
    {
        return msg;
    }
    
    @Override
    public void initialize(Size c, Field field)
    {
        min = c.min();
        max = c.max();
        msg = "属性" + field.getName() + "的值必须在" + c.min() + "到" + c.min() + "之间";
    }
    
    @Override
    public boolean isValid(Number value)
    {
        if (value == null || value.longValue() > max || value.longValue() < min)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
}
