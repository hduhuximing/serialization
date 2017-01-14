package com.jfireframework.context.cache.support;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.jfireframework.context.cache.Cache;
import com.jfireframework.context.cache.CacheManager;

public abstract class AbstractCacheManager implements CacheManager
{
    protected List<String>                 cacheNames = new LinkedList<String>();
    protected ConcurrentMap<String, Cache> cacheMap   = new ConcurrentHashMap<String, Cache>();
    
    public AbstractCacheManager()
    {
        cacheNames.add("default");
    }
    
    @Override
    public Cache get(String name)
    {
        return cacheMap.get(name);
    }
    
}
