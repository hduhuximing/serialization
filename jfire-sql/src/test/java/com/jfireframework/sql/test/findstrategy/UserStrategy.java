package com.jfireframework.sql.test.findstrategy;

import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.SqlStrategy;
import com.jfireframework.sql.annotation.Sqlstrategies;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "userstrategy")
@Sqlstrategies({ //
        @SqlStrategy(name = "test1", fields = "name,password|age,boy"), //
        @SqlStrategy(name = "test2", fields = "name,age,birthday|id"), //
        @SqlStrategy(name = "test3", fields = "id,password,age|boy"), //
        @SqlStrategy(name = "test4", fields = "password,age|age")
})
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
