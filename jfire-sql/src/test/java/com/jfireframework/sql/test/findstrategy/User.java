package com.jfireframework.sql.test.findstrategy;

import com.jfireframework.sql.annotation.FindByStrategy;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.StrategyBind;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User
{
    
    @Id
    @FindByStrategy("test2")
    private Integer id;
    @StrategyBind("test1,test2")
    private String  name;
    @StrategyBind("test1")
    private String  password;
    @StrategyBind("test1,test2")
    @FindByStrategy("test1")
    private Integer age;
    @StrategyBind("test2")
    private String  birthday;
    @FindByStrategy("test1")
    private boolean boy;
    
    public boolean isBoy()
    {
        return boy;
    }
    
    public void setBoy(boolean boy)
    {
        this.boy = boy;
    }
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public Integer getAge()
    {
        return age;
    }
    
    public void setAge(Integer age)
    {
        this.age = age;
    }
    
    public String getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }
    
}
