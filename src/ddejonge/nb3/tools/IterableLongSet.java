// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IterableLongSet.java

package ddejonge.nb3.tools;

import java.util.Iterator;

// Referenced classes of package ddejonge.nb3.tools:
//            LongSet

public class IterableLongSet extends LongSet
    implements Iterable
{
    public class LongSetIterator
        implements Iterator
    {

        public boolean hasNext()
        {
            for(int i = currentIndex; i < 64; i++)
                if(LongSet.containsElement(setId, i))
                {
                    nextElement = i;
                    return true;
                }

            nextElement = -1;
            return false;
        }

        public Integer next()
        {
            if(nextElement != -1 && nextElement >= currentIndex)
            {
                currentIndex = nextElement + 1;
                return Integer.valueOf(nextElement);
            }
            if(hasNext())
            {
                currentIndex = nextElement + 1;
                return Integer.valueOf(nextElement);
            } else
            {
                return Integer.valueOf(-1);
            }
        }

        public void remove()
        {
        }

        public volatile Object next()
        {
            return next();
        }

        private int currentIndex;
        private int nextElement;
        private long setId;
        final IterableLongSet this$0;

        public LongSetIterator(long setId)
        {
            this$0 = IterableLongSet.this;
            super();
            currentIndex = 0;
            nextElement = -1;
            this.setId = setId;
        }
    }


    public IterableLongSet()
    {
    }

    public Iterator iterator()
    {
        return new LongSetIterator(getId());
    }
}
