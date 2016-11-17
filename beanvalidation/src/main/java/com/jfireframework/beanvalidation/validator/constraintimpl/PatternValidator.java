package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import com.jfireframework.beanvalidation.constraint.Pattern;
import com.jfireframework.beanvalidation.validator.ConstraintValidator;

public class PatternValidator implements ConstraintValidator<Pattern, String>
{
    
    private java.util.regex.Pattern pattern;
    private String                  msg;
    
    @Override
    public String message()
    {
        return msg;
    }
    
    @Override
    public void initialize(Pattern c, Field field)
    {
        // 编译正则表达式
        pattern = java.util.regex.Pattern.compile(c.value());
        msg = field.getName() + "属性必须符合格式" + c.value();
    }
    
    @Override
    public boolean isValid(String value)
    {
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    
}
