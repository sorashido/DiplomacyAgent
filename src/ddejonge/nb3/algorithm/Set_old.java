// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Set_old.java

package ddejonge.nb3.algorithm;

import java.util.Arrays;
import java.util.Iterator;

public class Set_old
    implements Iterable
{
    public class SetIterator
        implements Iterator
    {

        public boolean hasNext()
        {
            for(int i = currentIndex; i < container.length; i++)
                if(container[i])
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

        private boolean container[];
        private int currentIndex;
        private int nextElement;
        final Set_old this$0;

        public SetIterator(boolean container[])
        {
            this$0 = Set_old.this;
            super();
            currentIndex = 0;
            nextElement = -1;
            this.container = container;
        }
    }


    public void setCapacity(int _maxNumElements)
    {
        if(capacity == -1)
            capacity = _maxNumElements;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public Set_old(int _maxNumElements)
    {
        capacity = -1;
        setCapacity(_maxNumElements);
        elements = new boolean[_maxNumElements];
    }

    public Set_old(int _maxNumElements, boolean full)
    {
        this(_maxNumElements);
        if(full)
            Arrays.fill(elements, true);
    }

    public Set_old(Set_old s)
    {
        capacity = -1;
        elements = s.toBoolArray();
    }

    public Set_old(int intArray[])
    {
        capacity = -1;
        for(int i = 0; i < intArray.length; i++)
        {
            int n = intArray[i];
            add(n);
        }

    }

    public boolean[] toBoolArray()
    {
        boolean returnArray[] = new boolean[capacity];
        for(int i = 0; i < capacity; i++)
            returnArray[i] = elements[i];

        return returnArray;
    }

    public void add(int newElement)
    {
        elements[newElement] = true;
    }

    public void remove(int oldElement)
    {
        elements[oldElement] = false;
    }

    public void add(Set_old s)
    {
        for(int i = 0; i < capacity; i++)
            if(s.contains(i))
                elements[i] = true;

    }

    public void clear()
    {
        Arrays.fill(elements, false);
    }

    public void makeEqualTo(Set_old other)
    {
        for(int i = 0; i < capacity; i++)
            elements[i] = other.contains(i);

    }

    public boolean contains(int x)
    {
        return elements[x];
    }

    public boolean isEmpty()
    {
        for(int i = 0; i < elements.length; i++)
            if(elements[i])
                return false;

        return true;
    }

    public int size()
    {
        int size = 0;
        for(int i = 0; i < capacity; i++)
            if(elements[i])
                size++;

        return size;
    }

    public Set_old copy()
    {
        return new Set_old(this);
    }

    public Set_old subtract(Set_old s)
    {
        Set_old newSet = new Set_old(capacity);
        for(int i = 0; i < capacity; i++)
            if(elements[i] && !s.contains(i))
                newSet.add(i);

        return newSet;
    }

    public Set_old complement(Set_old s)
    {
        Set_old newSet = new Set_old(capacity);
        for(int i = 0; i < capacity; i++)
            if(!elements[i])
                newSet.add(i);

        return newSet;
    }

    public static Set_old union(Set_old s1, Set_old s2)
    {
        Set_old returnSet = new Set_old(s1.capacity);
        for(int i = 0; i < s1.capacity; i++)
            if(s1.contains(i) || s2.contains(i))
                returnSet.add(i);

        return returnSet;
    }

    public static Set_old intersection(Set_old s1, Set_old s2)
    {
        Set_old returnSet = new Set_old(s1.capacity);
        for(int val = 0; val < s1.capacity; val++)
            if(s1.contains(val) && s2.contains(val))
                returnSet.add(val);

        return returnSet;
    }

    public boolean isDisjointWith(Set_old other)
    {
        for(int i = 0; i < elements.length; i++)
            if(elements[i] && other.elements[i])
                return false;

        return true;
    }

    public String toString()
    {
        String s = "{ ";
        for(int i = 0; i < capacity; i++)
            if(contains(i))
                s = (new StringBuilder(String.valueOf(s))).append(i).append(", ").toString();

        int n = s.lastIndexOf(',');
        if(n >= 1)
            s = s.substring(0, n);
        s = (new StringBuilder(String.valueOf(s))).append(" }").toString();
        return s;
    }

    public Iterator iterator()
    {
        return new SetIterator(elements);
    }

    private int capacity;
    private boolean elements[];
}
