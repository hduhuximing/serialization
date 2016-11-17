package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.AssertFalse;
import com.jfireframework.beanvalidation.ValidResult;

public class AssertFalaseValidator extends AbstractConstraintValidator<AssertFalse, Boolean>
{
    private String msg;
    
    @Override
    public void initialize(AssertFalse c, Field field)
    {
        msg = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(Boolean value, ValidResult result)
    {
        if (value == true)
        {
            result.setMessage(msg);
        }
        return !value;
    }
    
}
