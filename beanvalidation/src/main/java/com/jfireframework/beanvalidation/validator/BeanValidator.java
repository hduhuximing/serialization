package com.jfireframework.beanvalidation.validator;

public interface BeanValidator<T>
{
    public boolean validate(T entity, ValidResult result);
}
