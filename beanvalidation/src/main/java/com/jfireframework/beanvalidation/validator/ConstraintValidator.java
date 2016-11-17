package com.jfireframework.beanvalidation.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.ValidResult;

public interface ConstraintValidator<C extends Annotation, V>
{
    public void initialize(C c, Field field);
    
    public boolean isValid(V value, ValidResult result);
}
