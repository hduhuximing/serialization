package com.jfireframework.beanvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import com.jfireframework.beanvalidation.constraint.Length;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.AssertFalaseValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.AssertTrueValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.LengthValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.MaxValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.MinValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.NotNullValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.NullValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.PatternValidator;

public class ConstraintValidatorFactory
{
    @SuppressWarnings("unchecked")
    public static <C extends Annotation, V> ConstraintValidator<C, V> build(Field field, C constraint)
    {
        ConstraintValidator<C, V> validator = null;
        if (constraint instanceof AssertFalse)
        {
            validator = (ConstraintValidator<C, V>) new AssertFalaseValidator();
        }
        else if (constraint instanceof AssertTrue)
        {
            validator = (ConstraintValidator<C, V>) new AssertTrueValidator();
        }
        else if (constraint instanceof Length)
        {
            validator = (ConstraintValidator<C, V>) new LengthValidator();
        }
        else if (constraint instanceof Max)
        {
            validator = (ConstraintValidator<C, V>) new MaxValidator();
        }
        else if (constraint instanceof Min)
        {
            validator = (ConstraintValidator<C, V>) new MinValidator();
        }
        else if (constraint instanceof NotNull)
        {
            validator = (ConstraintValidator<C, V>) new NotNullValidator();
        }
        else if (constraint instanceof Null)
        {
            validator = (ConstraintValidator<C, V>) new NullValidator();
        }
        else if (constraint instanceof Pattern)
        {
            validator = (ConstraintValidator<C, V>) new PatternValidator();
        }
        else
        {
            throw new NullPointerException();
        }
        validator.initialize(constraint, field);
        return validator;
    }
}
