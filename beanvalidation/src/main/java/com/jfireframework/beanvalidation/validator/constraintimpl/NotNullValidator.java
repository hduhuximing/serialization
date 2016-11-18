package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.NotNull;

public class NotNullValidator extends AbstractConstraintValidator<NotNull, Object>
{
    
    @Override
    public void initialize(NotNull c, Field field)
    {
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(Object value)
    {
        return value == null ? false : true;
    }
    
}
