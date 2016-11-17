package com.jfireframework.beanvalidation.validator;

import com.jfireframework.beanvalidation.ValidResult;

public interface BeanValidator<T>
{
    public boolean isValid(T entity, ValidResult result);
}
