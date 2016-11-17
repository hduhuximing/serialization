package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.AssertFalse;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class AssertFalaseValidator implements ConstraintValidator<AssertFalse, Boolean>
{
    
    @Override
    public String message()
    {
        return "必须为false";
    }
    
    @Override
    public void initialize(AssertFalse c, Field field)
    {
        
    }
    
    @Override
    public boolean isValid(Boolean value)
    {
        return !value;
    }
    
}
