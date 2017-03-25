package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class SpscQueue<E> implements Queue<E>
{
    private CpuCachePadingRefence<Node> head;
    private CpuCachePadingRefence<Node> tail;
    
    public SpscQueue()
    {
        Node init = new Node(null);
        head = new CpuCachePadingRefence<SpscQueue.Node>(init);
        tail = new CpuCachePadingRefence<SpscQueue.Node>(init);
    }
    
    public boolean offer(E e)
    {
        Node t = tail.get();
        Node insert = new Node(e);
        t.next = insert;
        tail.orderSet(insert);
        return true;
    }
    
    public E poll()
    {
        Node h = head.get();
        Node hn = h.next;
        if (hn != null)
        {
            @SuppressWarnings("unchecked")
            E e = (E) hn.item;
            head.orderSet(hn);
            h.forget();
            return e;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public int drain(E[] array, int limit)
    {
        Node h, hn;
        int i = 0;
        for (hn = (h = head.get()); i < limit; i++, hn = (h = hn).next)
        {
            if (hn != null)
            {
                Object e = hn.item;
                array[i] = (E) e;
                head.orderSet(hn);
                h.forget();
            }
            else
            {
                head.orderSet(h);
                return i;
            }
        }
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
        return head.get().next == null;
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
