package com.jfireframework.mvc.binder.field.extra;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.impl.ObjectDataBinder;
import com.jfireframework.mvc.binder.resolver.ParamResolver;
import com.jfireframework.mvc.binder.resolver.TreeValueResolver;

public class ObjectField extends AbstractBinderField
{
    private final ObjectDataBinder binder;
    
    public ObjectField(Field field)
    {
        super(field);
        binder = new ObjectDataBinder(field.getType(), "", null);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamResolver node, Object entity)
    {
        Object value = binder.bind(request, (TreeValueResolver) node, response);
        unsafe.putObject(entity, offset, value);
    }
    
}
