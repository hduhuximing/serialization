package com.jfireframework.beanvalidation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface Constraint
{
    public Class<? extends ConstraintValidator<? extends Annotation, ?>>[] validateBy();
}
