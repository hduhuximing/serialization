package com.jfireframework.sql.extra.dbstructure;

import com.jfireframework.baseutil.collection.StringCache;

public class DefaultNameStrategy implements ColNameStrategy
{
    
    @Override
    public String toDbName(String name)
    {
        StringCache cache = new StringCache(20);
        int index = 0;
        while (index < name.length())
        {
            char c = name.charAt(index);
            if (c >= 'A' && c <= 'Z')
            {
                cache.append('_').append(Character.toLowerCase(c));
            }
            else
            {
                cache.append(c);
            }
            index += 1;
        }
        return cache.toString();
    }
    
}
