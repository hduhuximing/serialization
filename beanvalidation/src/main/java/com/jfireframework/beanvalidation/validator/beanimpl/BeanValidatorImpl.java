package com.jfireframework.beanvalidation.validator.beanimpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.beanvalidation.constraint.AssertFalse;
import com.jfireframework.beanvalidation.constraint.AssertTrue;
import com.jfireframework.beanvalidation.constraint.Constraint;
import com.jfireframework.beanvalidation.constraint.Length;
import com.jfireframework.beanvalidation.constraint.Max;
import com.jfireframework.beanvalidation.constraint.Min;
import com.jfireframework.beanvalidation.constraint.NotNull;
import com.jfireframework.beanvalidation.constraint.Null;
import com.jfireframework.beanvalidation.constraint.Pattern;
import com.jfireframework.beanvalidation.constraint.Size;
import com.jfireframework.beanvalidation.validator.BeanValidator;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;
import com.jfireframework.beanvalidation.validator.ValidResult;
import com.jfireframework.beanvalidation.validator.constraintimpl.AssertFalaseValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.AssertTrueValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.LengthValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.MaxValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.MinValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.NotNullValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.NullValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.PatternValidator;
import com.jfireframework.beanvalidation.validator.constraintimpl.SizeValidator;

@SuppressWarnings("unchecked")
public class BeanValidatorImpl<T> implements BeanValidator<T>
{
    private final Field[]                                                                             fields;
    private final ConstraintValidator<?, ?>[][]                                                       constraintValidators;
    private static final Map<Class<? extends Annotation>, Class<? extends ConstraintValidator<?, ?>>> map = new HashMap<Class<? extends Annotation>, Class<? extends ConstraintValidator<?, ?>>>();
    
    static
    {
        map.put(AssertFalse.class, AssertFalaseValidator.class);
        map.put(AssertTrue.class, AssertTrueValidator.class);
        map.put(Length.class, LengthValidator.class);
        map.put(Max.class, MaxValidator.class);
        map.put(Min.class, MinValidator.class);
        map.put(NotNull.class, NotNullValidator.class);
        map.put(Null.class, NullValidator.class);
        map.put(Pattern.class, PatternValidator.class);
        map.put(Size.class, SizeValidator.class);
        
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
    public boolean validate(T entity, ValidResult result)
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
            for (Entry<Class<? extends Annotation>, Class<? extends ConstraintValidator<?, ?>>> entry : map.entrySet())
            {
                if (AnnotationUtil.isPresent(entry.getKey(), field))
                {
                    ConstraintValidator<Annotation, ?> validator = (ConstraintValidator<Annotation, ?>) entry.getValue().newInstance();
                    validator.initialize(AnnotationUtil.getAnnotation(entry.getKey(), field), field);
                    list.add(validator);
                }
            }
            if (AnnotationUtil.isPresent(Constraint.class, field))
            {
                Constraint constraint = AnnotationUtil.getAnnotation(Constraint.class, field);
                Class<? extends ConstraintValidator<?, ?>>[] types = constraint.validatedBy();
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
