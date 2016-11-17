package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.Null;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class NullValidator implements ConstraintValidator<Null, Object>
{
    
    private String msg;
    
    @Override
    public String message()
    {
        return msg;
    }
    
    @Override
    public void initialize(Null c, Field field)
    {
        msg = field.getName() + "必须为null";
    }
    
    @Override
    public boolean isValid(Object value)
    {
        return value == null ? true : false;
    }
}
