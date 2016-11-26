package com.jfireframework.context.bean.impl;

import java.util.HashMap;
import com.jfireframework.context.bean.Bean;

public abstract class BaseBean extends BeanInitProcessImpl implements Bean
{
    /* bean对象初始化过程中暂存生成的中间对象 */
    protected final ThreadLocal<HashMap<String, Object>> beanInstanceMap = new ThreadLocal<HashMap<String, Object>>() {
                                                                             @Override
                                                                             protected HashMap<String, Object> initialValue()
                                                                             {
                                                                                 return new HashMap<String, Object>();
                                                                             }
                                                                         };
    /** 单例的引用对象 */
    protected Object                                     singletonInstance;
    
}
