package com.jfireframework.licp;

public interface LicpInterceptor<V>
{
    /**
     * 需要拦截的字段。规则是类的全限定名
     * 
     * @return
     */
    public Class<V> rule();
    
    /**
     * 在序列化一个对象前对这个对象进行拦截处理
     * 
     * @param v
     * @return
     */
    public V serialize(V v);
    
    /**
     * 在逆序列化一个对象之后，在返回对象之前对该对象进行拦截处理
     * 
     * @param v
     * @return
     */
    public V deserialize(V v);
    
}
