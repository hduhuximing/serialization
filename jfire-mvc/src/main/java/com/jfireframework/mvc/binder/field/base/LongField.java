package com.jfireframework.mvc.binder.field.base;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;

public class LongField extends AbstractBinderField
{
    public LongField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamResolver node, Object entity)
    {
        String value = ((StringValueResolver) node).getValue();
        unsafe.putLong(entity, offset, Long.valueOf(value).longValue());
    }
    
}
