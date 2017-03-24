package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

/**
 * Created by 林斌 on 2016/9/10.
 */
public class MPSCQueue<E>
{
    private CpuCachePadingRefence<MPSCNode> head;
    private CpuCachePadingRefence<MPSCNode> tail;
    private static final Unsafe             unsafe = ReflectUtil.getUnsafe();
    private static final long               offset = ReflectUtil.getFieldOffset("tail", MPSCQueue.class);
    
    public MPSCQueue()
    {
        MPSCNode initialize = new MPSCNode(null);
        head = new CpuCachePadingRefence<MPSCQueue.MPSCNode>(initialize);
        tail = new CpuCachePadingRefence<MPSCQueue.MPSCNode>(initialize);
    }
    
    public void offer(E value)
    {
        MPSCNode insert = new MPSCNode(value);
        MPSCNode t, p, pn;
        p = t = tail.get();
        if ((pn = p.next) == null && p.casNext(pn, insert))
        {
            return;
        }
        retry: //
        for (p = t = tail.get();;)
        {
            if ((pn = p.next) == null && p.casNext(null, insert))
            {
                return;
            }
            else if (pn != p && pn != null)
            {
                p = pn;
            }
            else
            {
                p = pn;
            }
        }
    }
    
    static class MPSCNode
    {
        private volatile Object   value;
        private volatile MPSCNode next;
        private static final long nextOff  = ReflectUtil.getFieldOffset("next", MPSCNode.class);
        private static final long valueOff = ReflectUtil.getFieldOffset("value", MPSCNode.class);
        
        public MPSCNode(Object value)
        {
            // 初始化的时候还不可见，可以使用松弛的写法
            unsafe.putObject(this, valueOff, value);
        }
        
        public boolean casNext(MPSCNode originNext, MPSCNode nowNext)
        {
            return unsafe.compareAndSwapObject(this, nextOff, originNext, nowNext);
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
    
}
