package com.jfireframework.codejson.function.impl.write.array;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.tracker.Tracker;

public class StringArrayWriter extends WriterAdapter
{
    
    @Override
    public void write(Object field, StringCache cache, Object entity, Tracker tracker)
    {
        String[] array = (String[]) field;
        cache.append('[');
        for (String each : array)
        {
            cache.append('"').append(each).append("\",");
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append(']');
    }
    
}
