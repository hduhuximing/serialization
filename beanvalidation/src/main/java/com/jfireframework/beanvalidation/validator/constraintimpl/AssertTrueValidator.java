package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.AssertTrue;

public class AssertTrueValidator extends AbstractConstraintValidator<AssertTrue, Boolean>
{
    
    @Override
    public void initialize(AssertTrue c, Field field)
    {
        message = field.getName() + "必须为真";
    }
    
    @Override
    public boolean isValid(Boolean value)
    {
        return value;
    }
    
}
