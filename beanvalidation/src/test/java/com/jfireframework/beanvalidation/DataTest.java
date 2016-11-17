package com.jfireframework.beanvalidation;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.beanvalidation.validator.BeanValidator;
import com.jfireframework.beanvalidation.validator.ValidResult;

public class DataTest
{
    @Test
    public void test()
    {
        BeanValidator<Data> validator = ValidatorFactory.build(Data.class);
        Data data = new Data();
        data.setAge(18);
        data.setName("linbin");
        ValidResult result = new ValidResult();
        validator.validate(data, result);
        Assert.assertTrue(result.isValid());
        data.setAge(30);
        validator.validate(data, result);
        Assert.assertFalse(result.isValid());
        System.out.println(result.getMessage());
    }
}
