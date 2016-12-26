package com.jfireframework.sql.test.findstrategy;

import com.jfireframework.sql.annotation.FindStrategy;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.annotation.UpdateStrategy;
import com.jfireframework.sql.annotation.SqlStrategy;

@SqlStrategy(
        findStrategies = { //
                @FindStrategy(name = "test1", selectFields = "name,password", whereFields = "age,boy"), //
                @FindStrategy(name = "test2", selectFields = "name,age,birthday", whereFields = "id"), //
                @FindStrategy(name = "test3", selectFields = "id,password,age", whereFields = "boy"),
        }, //
        updateStrategies = { //
                @UpdateStrategy(name = "test4", setFields = "password,age", whereFields = "age")
        }
)
@TableEntity(name = "userstrategy")
public class UserStrategy
{
    
    @Id
    private Integer id;
    private String  name;
    private String  password;
    private Integer age;
    private String  birthday;
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
