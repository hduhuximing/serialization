package com.jfireframework.mvc.binder.field.array.base;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.array.AbstractArrayField;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;

public class ArrayWFloatField extends AbstractArrayField
{
    
    public ArrayWFloatField(Field field)
    {
        super(field);
    }
    
    @Override
    protected Object buildByString(String str)
    {
        return Float.valueOf(str);
    }
    
    @Override
    protected Object buildByNode(ParamResolver node, HttpServletRequest request, HttpServletResponse response)
    {
        String value = ((StringValueResolver) node).getValue();
        return buildByString(value);
    }
}
