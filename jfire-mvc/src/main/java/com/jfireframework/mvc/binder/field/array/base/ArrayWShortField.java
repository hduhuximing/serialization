package com.jfireframework.mvc.binder.field.array.base;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.array.AbstractArrayField;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;

public class ArrayWShortField extends AbstractArrayField
{
    
    public ArrayWShortField(Field field)
    {
        super(field);
    }
    
    @Override
    protected Object buildByString(String str)
    {
        return Short.valueOf(str);
    }
    
    @Override
    protected Object buildByNode(ParamResolver node, HttpServletRequest request, HttpServletResponse response)
    {
        String value = ((StringValueResolver) node).getValue();
        return buildByString(value);
    }
}
