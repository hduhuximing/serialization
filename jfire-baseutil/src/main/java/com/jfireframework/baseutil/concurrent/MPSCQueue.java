package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

/**
 * Created by 林斌 on 2016/9/10.
 */
public class MPSCQueue<E> implements Queue<E>
{
    private CpuCachePadingRefence<MPSCNode> head;
    private CpuCachePadingRefence<MPSCNode> tail;
    private static final Unsafe             unsafe = ReflectUtil.getUnsafe();
    
    public MPSCQueue()
    {
        MPSCNode initialize = new MPSCNode(null);
        head = new CpuCachePadingRefence<MPSCQueue.MPSCNode>(initialize);
        tail = new CpuCachePadingRefence<MPSCQueue.MPSCNode>(initialize);
    }
    
    @SuppressWarnings("unchecked")
    public int drain(E[] array, int limit)
    {
        MPSCNode h, hn, p;
        int i = 0;
        for (hn = (p = h = head.get()).next; i < limit && hn != null; i++, p = h, hn = (h = hn).next)
        {
            Object e = hn.value;
            array[i] = (E) e;
        }
        if (p != h)
        {
            p.forget();
        }
        head.ordinarySet(h);
        return i;
    }
    
    @SuppressWarnings("unchecked")
    public E poll()
    {
        MPSCNode h = head.get();
        MPSCNode hn = h.next;
        if (hn != null)
        {
            Object e = hn.value;
            head.ordinarySet(hn);
            h.forget();
            return (E) e;
        }
        return null;
    }
    
    public boolean offer(E value)
    {
        MPSCNode insert = new MPSCNode(value);
        MPSCNode p, t, pn;
        for (p = t = tail.get();;)
        {
            if ((pn = p.next) != null)
            {
                p = (pn != p) ? pn : (t = tail.get()).next == t ? t = head.get() : t;
            }
            else if (!p.casNext(null, insert))
            {
                p = p.next;
            }
            else
            {
                if (p != t)
                {
                    while ((t != tail.get() || !tail.compareAndSwap(t, insert)) && //
                            (insert = (t = tail.get()).next) != null && //
                            (insert = insert.next) != null && insert != t)
                        ;
                }
                return true;
            }
        }
    }
    
    static class MPSCNode
    {
        private Object            value;
        private volatile MPSCNode next;
        private static final long nextOff  = ReflectUtil.getFieldOffset("next", MPSCNode.class);
        private static final long valueOff = ReflectUtil.getFieldOffset("value", MPSCNode.class);
        
        public MPSCNode(Object value)
        {
            this.value = value;
        }
        
        public boolean casNext(MPSCNode originNext, MPSCNode nowNext)
        {
            return unsafe.compareAndSwapObject(this, nextOff, originNext, nowNext);
        }
        
        public void forget()
        {
            unsafe.putObject(this, valueOff, this);
            unsafe.putObject(this, nextOff, this);
            
        }
        
        public void forgetValue()
        {
            unsafe.putObject(this, valueOff, this);
        }
        
        public void forgetNext()
        {
            unsafe.putObject(this, nextOff, this);
        }
    }
    
    @Override
    public int size()
    {
        int count = 0;
        MPSCNode h = head.get();
        do
        {
            MPSCNode hn = h.next;
            if (hn != null)
            {
                count += 1;
                h = hn;
            }
            else
            {
                break;
            }
        } while (true);
        return count;
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
    
    @SuppressWarnings("unchecked")
    @Override
    public E peek()
    {
        MPSCNode h = head.get();
        MPSCNode hn = h.next;
        if (hn != null)
        {
            Object e = hn.value;
            return (E) e;
        }
        return null;
    }
    
}
