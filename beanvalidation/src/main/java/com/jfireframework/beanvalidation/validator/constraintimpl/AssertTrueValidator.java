package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.AssertTrue;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class AssertTrueValidator implements ConstraintValidator<AssertTrue, Boolean>
{
    
    @Override
    public String message()
    {
        return "必须为真";
    }
    
    @Override
    public void initialize(AssertTrue c, Field field)
    {
        
    }
    
    @Override
    public boolean isValid(Boolean value)
    {
        return value;
    }
    
}
