package com.jfireframework.beanvalidation.validator.beanimpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.beanvalidation.ConstraintValidatorFactory;
import com.jfireframework.beanvalidation.ValidResult;
import com.jfireframework.beanvalidation.constraint.Constraint;
import com.jfireframework.beanvalidation.validator.BeanValidator;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

@SuppressWarnings("unchecked")
public class BeanValidatorImpl<T> implements BeanValidator<T>
{
    private final Field[]                                 fields;
    private final ConstraintValidator<?, ?>[][]           constraintValidators;
    private static final Set<Class<? extends Annotation>> constraintSet = new HashSet<Class<? extends Annotation>>();
    static
    {
        constraintSet.add(AssertFalse.class);
        constraintSet.add(AssertTrue.class);
        constraintSet.add(Max.class);
        constraintSet.add(Min.class);
        constraintSet.add(Null.class);
        constraintSet.add(NotNull.class);
        constraintSet.add(Pattern.class);
    }
    
    public BeanValidatorImpl(Class<T> type)
    {
        fields = ReflectUtil.getAllFields(type);
        constraintValidators = new ConstraintValidator[fields.length][];
        int i = 0;
        for (Field each : fields)
        {
            each.setAccessible(true);
            constraintValidators[i] = getFromField(each);
            i++;
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isValid(T entity, ValidResult result)
    {
        try
        {
            int i = 0;
            for (Field each : fields)
            {
                Object value = each.get(entity);
                for (ConstraintValidator validator : constraintValidators[i])
                {
                    if (validator.isValid(value) == false)
                    {
                        result.setMessage(validator.message());
                        result.setValid(false);
                        return false;
                    }
                }
                i++;
            }
            return true;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    private ConstraintValidator[] getFromField(Field field)
    {
        try
        {
            List<ConstraintValidator<?, ?>> list = new LinkedList<ConstraintValidator<?, ?>>();
            for (Class<? extends Annotation> each : constraintSet)
            {
                if (AnnotationUtil.isPresent(each, field))
                {
                    Annotation constraint = AnnotationUtil.getAnnotation(each, field);
                    ConstraintValidator<Annotation, ?> validator = ConstraintValidatorFactory.build(field, constraint);
                    list.add(validator);
                }
            }
            if (AnnotationUtil.isPresent(Constraint.class, field))
            {
                Constraint constraint = AnnotationUtil.getAnnotation(Constraint.class, field);
                Class<? extends ConstraintValidator<?, ?>>[] types = constraint.validateBy();
                for (Class<? extends ConstraintValidator<?, ?>> each : types)
                {
                    ConstraintValidator<Annotation, ?> validator = (ConstraintValidator<Annotation, ?>) each.newInstance();
                    validator.initialize(constraint, field);
                    list.add(validator);
                }
            }
            return list.toArray(new ConstraintValidator[list.size()]);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
}
