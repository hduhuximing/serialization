package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.Null;

public class NullValidator extends AbstractConstraintValidator<Null, Object>
{
    
    @Override
    public void initialize(Null c, Field field)
    {
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(Object value)
    {
        return value == null ? true : false;
    }
}
