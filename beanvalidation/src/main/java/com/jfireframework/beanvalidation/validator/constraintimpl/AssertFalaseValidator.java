package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.AssertFalse;

public class AssertFalaseValidator extends AbstractConstraintValidator<AssertFalse, Boolean>
{
    
    @Override
    public void initialize(AssertFalse c, Field field)
    {
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(Boolean value)
    {
        return !value;
    }
    
}
