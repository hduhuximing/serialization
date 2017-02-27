package com.jfireframework.mvc.binder.field.wrapper;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.StringValueResolver;

public class WCharacterField extends AbstractBinderField
{
    
    public WCharacterField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamResolver node, Object entity)
    {
        String value = ((StringValueResolver) node).getValue();
        Character c = value.charAt(0);
        unsafe.putObject(entity, offset, c);
    }
    
}
