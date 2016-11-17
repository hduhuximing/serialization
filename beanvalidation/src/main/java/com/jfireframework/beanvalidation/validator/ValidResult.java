package com.jfireframework.beanvalidation.validator;

public class ValidResult
{
    private String  message;
    private boolean valid = true;
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }
    
}
