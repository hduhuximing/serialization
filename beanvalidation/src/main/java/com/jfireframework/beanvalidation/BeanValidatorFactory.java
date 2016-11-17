package com.jfireframework.beanvalidation;

import com.jfireframework.beanvalidation.validator.BeanValidator;
import com.jfireframework.beanvalidation.validator.beanimpl.BeanValidatorImpl;

public class BeanValidatorFactory
{
    public static <T> BeanValidator<T> build(Class<T> type)
    {
        return new BeanValidatorImpl<T>(type);
    }
}
