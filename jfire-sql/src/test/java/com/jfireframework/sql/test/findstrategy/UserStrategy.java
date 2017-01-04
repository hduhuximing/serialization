package com.jfireframework.sql.test.findstrategy;

import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.SqlStrategy;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "userstrategy")
public class UserStrategy
{
    public static enum SS implements SqlStrategy
    {
        test1("name,password", "age,boy"), //
        test2("name,age,birthday", "id"), //
        test3("id,password,age", "boy"), //
        test4("password,age", "age");
        private final String valueFields;
        private final String whereFields;
        
        private SS(String selectFields, String whereFields)
        {
            this.valueFields = selectFields;
            this.whereFields = whereFields;
        }
        
        @Override
        public String valueFields()
        {
            return valueFields;
        }
        
        @Override
        public String whereFields()
        {
            return whereFields;
        }
        
    }
    
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
