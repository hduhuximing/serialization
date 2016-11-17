package com.jfireframework.beanvalidation.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface ConstraintValidator<C extends Annotation, V>
{
    public String message();
    
    public void initialize(C c, Field field);
    
    public boolean isValid(V value);
}
