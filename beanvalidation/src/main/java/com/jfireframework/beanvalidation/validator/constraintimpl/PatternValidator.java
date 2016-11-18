package com.jfireframework.beanvalidation.validator.constraintimpl;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import javax.validation.constraints.Pattern;

public class PatternValidator extends AbstractConstraintValidator<Pattern, String>
{
    
    private java.util.regex.Pattern pattern;
    
    @Override
    public void initialize(Pattern c, Field field)
    {
        // 编译正则表达式
        pattern = java.util.regex.Pattern.compile(c.regexp());
        message = getMessage(c.message());
    }
    
    @Override
    public boolean isValid(String value)
    {
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    
}
