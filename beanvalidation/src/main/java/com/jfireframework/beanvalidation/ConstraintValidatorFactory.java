package com.jfireframework.beanvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import com.jfireframework.beanvalidation.constraint.AssertFalse;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.AssertFalaseValidator;

public class ConstraintValidatorFactory
{
    public static <C extends Annotation, V> ConstraintValidator<C, V> build(Field field, C constraint)
    {
        if (constraint instanceof AssertFalse)
        {
            AssertFalaseValidator validator = new AssertFalaseValidator();
            validator.initialize((AssertFalse) constraint, field);
            return (ConstraintValidator<C, V>) validator;
        }
    }
}
