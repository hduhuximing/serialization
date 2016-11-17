package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.NotNull;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class NotNullValidator implements ConstraintValidator<NotNull, Object>
{
    private String msg;
    
    @Override
    public String message()
    {
        return msg;
    }
    
    @Override
    public void initialize(NotNull c, Field field)
    {
        msg = field.getName() + "不能为null";
    }
    
    @Override
    public boolean isValid(Object value)
    {
        return value == null ? false : true;
    }
    
}
