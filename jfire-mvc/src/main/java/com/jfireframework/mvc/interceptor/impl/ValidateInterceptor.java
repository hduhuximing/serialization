package com.jfireframework.mvc.interceptor.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.beanvalidation.ValidResult;
import com.jfireframework.beanvalidation.validator.BeanValidator;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.interceptor.ActionInterceptor;

@Resource
public class ValidateInterceptor implements ActionInterceptor
{
    
    @Override
    public int getOrder()
    {
        return 13;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean interceptor(HttpServletRequest request, HttpServletResponse response, Action action)
    {
        int[] validatorIndexs = action.getValidatorIndexs();
        if (validatorIndexs.length > 0)
        {
            BeanValidator[] validators = action.getValidators();
            Object[] params = (Object[]) request.getAttribute(DataBinderInterceptor.DATABINDERKEY);
            int index = 0;
            for (int each : validatorIndexs)
            {
                ValidResult result = (ValidResult) params[each];
                Object entity = params[each - 1];
                validators[index].isValid(entity, result);
            }
        }
        
        return true;
    }
    
    @Override
    public String includePath()
    {
        return "*";
    }
    
    @Override
    public String excludePath()
    {
        return null;
    }
    
    @Override
    public String tokenRule()
    {
        return null;
    }
    
}
