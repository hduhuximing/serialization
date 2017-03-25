package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class SpscQueue<E> implements Queue<E>
{
    Node head;
    Node tail;
    
    public SpscQueue()
    {
        Node init = new Node(null);
        head = tail = init;
    }
    
    public boolean offer(E e)
    {
        Node insert = new Node(e);
        tail.next = insert;
        tail = insert;
        return true;
    }
    
    public E poll()
    {
        Node hn = head.next;
        if (hn != null)
        {
            @SuppressWarnings("unchecked")
            E e = (E) hn.item;
            head.forget();
            head = hn;
            return e;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public int drain(E[] array, int limit)
    {
        Node h, hn, p;
        int i = 0;
        for (hn = (p = h = head).next; i < limit && hn != null; i++, p = h, hn = (h = hn).next)
        {
            Object e = hn.item;
            array[i] = (E) e;
        }
        if (p != h)
        {
            p.forget();
        }
        head = h;
        return i;
    }
    
    private static class Node
    {
        Object                      item;
        volatile Node               next;
        private static final long   offset = ReflectUtil.getFieldOffset("next", Node.class);
        private static final Unsafe unsafe = ReflectUtil.getUnsafe();
        
        public Node(Object item)
        {
            this.item = item;
        }
        
        public void forget()
        {
            item = this;
            unsafe.putObject(this, offset, this);
        }
    }
    
    @Override
    public int size()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public boolean isEmpty()
    {
        return head.next == null;
    }
    
    @Override
    public boolean contains(Object o)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public Iterator<E> iterator()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object[] toArray()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public <T> T[] toArray(T[] a)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean remove(Object o)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean containsAll(Collection<?> c)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean removeAll(Collection<?> c)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean retainAll(Collection<?> c)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void clear()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean add(E e)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public E remove()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public E element()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public E peek()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
