package com.jfireframework.beanvalidation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Constraint
{
    public Class<? extends ConstraintValidator<?, ?>>[] validatedBy();
}
