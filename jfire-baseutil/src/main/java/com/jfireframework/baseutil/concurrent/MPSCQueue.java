package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import com.jfireframework.baseutil.concurrent.MPSCQueue.MPSCNode;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

abstract class HeadLeftPad
{
    public volatile long p1, p2, p3, p4, p5, p6, p7;
    
    public long sumHeadLeftPad()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }
}

abstract class Head extends HeadLeftPad
{
    public volatile int         leftP;
    protected volatile MPSCNode head;
    public volatile int         rightP;
    
    public int sumHead()
    {
        return leftP + rightP;
    }
}

abstract class HeadRightPad extends Head
{
    public volatile long p11, p21, p31, p41, p51, p61, p71;
    
    public long sumHeadRightPad()
    {
        return p11 + p21 + p31 + p41 + p51 + p61 + p71;
    }
}

abstract class Tail extends HeadRightPad
{
    public volatile int         leftP1;
    protected volatile MPSCNode tail;
    public volatile int         rightP1;
    
    public int sumTail()
    {
        return leftP1 + rightP1;
    }
}

/**
 * Created by 林斌 on 2016/9/10.
 */
public class MPSCQueue<E> extends Tail implements Queue<E>
{
    public volatile long p01, p02, p03, p04, p05, p06, p07;
    
    public long fill()
    {
        return p01 + p02 + p03 + p04 + p05 + p06 + p07;
    }
    
    private static final long   headOff = ReflectUtil.getFieldOffset("head", Head.class);
    private static final long   tailOff = ReflectUtil.getFieldOffset("tail", Tail.class);
    private static final Unsafe unsafe  = ReflectUtil.getUnsafe();
    
    public MPSCQueue()
    {
        tail = head = new MPSCNode(null);
    }
    
    private void slackSetHead(MPSCNode h)
    {
        unsafe.putObject(this, headOff, h);
    }
    
    private boolean casTail(MPSCNode expect, MPSCNode now)
    {
        return unsafe.compareAndSwapObject(this, tailOff, expect, now);
    }
    
    @SuppressWarnings("unchecked")
    public int drain(E[] array, int limit)
    {
        MPSCNode h, hn, p;
        int i = 0;
        for (hn = (p = h = head).next; i < limit && hn != null; i++, p = h, hn = (h = hn).next)
        {
            Object e = hn.value;
            array[i] = (E) e;
        }
        if (p != h)
        {
            p.forget();
        }
        slackSetHead(h);
        return i;
    }
    
    @SuppressWarnings("unchecked")
    public E poll()
    {
        MPSCNode h = head;
        MPSCNode hn = h.next;
        if (hn != null)
        {
            Object e = hn.value;
            slackSetHead(hn);
            h.forget();
            return (E) e;
        }
        return null;
    }
    
    public boolean offer(E value)
    {
        MPSCNode insert = new MPSCNode(value);
        MPSCNode p, t, pn;
        for (p = t = tail;;)
        {
            if ((pn = p.next) != null)
            {
                p = (pn != p) ? pn : (t = tail).next == t ? t = head : t;
            }
            else if (!p.casNext(null, insert))
            {
                p = p.next;
            }
            else
            {
                if (p != t)
                {
                    while ((t != tail || !casTail(t, insert)) && //
                            (insert = (t = tail).next) != null && //
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
        MPSCNode h = head;
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
    
    @SuppressWarnings("unchecked")
    @Override
    public E peek()
    {
        MPSCNode h = head;
        MPSCNode hn = h.next;
        if (hn != null)
        {
            Object e = hn.value;
            return (E) e;
        }
        return null;
    }
    
}
