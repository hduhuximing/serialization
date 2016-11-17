package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import javax.validation.constraints.AssertTrue;
import com.jfireframework.beanvalidation.ValidResult;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class AssertTrueValidator implements ConstraintValidator<AssertTrue, Boolean>
{
    private String message;
    
    @Override
    public void initialize(AssertTrue c, Field field)
    {
        message = field.getName() + "必须为真";
    }
    
    @Override
    public boolean isValid(Boolean value, ValidResult result)
    {
        if (value == false)
        {
            result.setMessage(message);
        }
        return value;
    }
    
}
