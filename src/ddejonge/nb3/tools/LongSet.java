// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LongSet.java

package ddejonge.nb3.tools;


public class LongSet
{

    public LongSet()
    {
        setId = 0L;
    }

    public LongSet(long setId)
    {
        this.setId = setId;
    }

    public LongSet(LongSet original)
    {
        setId = original.getId();
    }

    public long getId()
    {
        return setId;
    }

    public void fill(int size)
    {
        setId = getFilledSet(size);
    }

    public void add(int i)
    {
        setId = addElement(setId, i);
    }

    public boolean contains(int i)
    {
        return containsElement(setId, i);
    }

    public void remove(int i)
    {
        setId = removeElement(setId, i);
    }

    public void addAll(long longSetId)
    {
        setId = getUnion(setId, longSetId);
    }

    public void addAll(LongSet longSet)
    {
        setId = getUnion(setId, longSet.setId);
    }

    public boolean containsAll(long longSetId)
    {
        return containsSubset(setId, longSetId);
    }

    public boolean containsAll(LongSet longSet)
    {
        return containsSubset(setId, longSet.setId);
    }

    public void removeAll(long longSetId)
    {
        setId = removeSet(setId, longSetId);
    }

    public void removeAll(LongSet longSet)
    {
        setId = removeSet(setId, longSet.getId());
    }

    public void clear()
    {
        setId = 0L;
    }

    public boolean isEmpty()
    {
        return setId == 0L;
    }

    public void makeEqualTo(LongSet longSet)
    {
        setId = longSet.setId;
    }

    public void makeEqualTo(long id)
    {
        setId = id;
    }

    public int size()
    {
        int size = 0;
        for(int i = 0; i < 64; i++)
            if(containsElement(setId, i))
                size++;

        return size;
    }

    public String toString()
    {
        String s = "{";
        for(int i = 0; i < 64; i++)
            if(containsElement(setId, i))
                s = (new StringBuilder(String.valueOf(s))).append(i).append(", ").toString();

        if(s.length() > 1)
            s = s.substring(0, s.length() - 2);
        s = (new StringBuilder(String.valueOf(s))).append("}").toString();
        return s;
    }

    public LongSet copy()
    {
        return new LongSet(setId);
    }

    public LongSet subtract(LongSet longSet)
    {
        long newSetId = removeSet(setId, longSet.setId);
        return new LongSet(newSetId);
    }

    public static long addElement(long set, int elementId)
    {
        return set | 1L << elementId;
    }

    public static boolean containsElement(long set, int elementId)
    {
        return (1L << elementId & set) == 1L << elementId;
    }

    public static long removeElement(long set, int elementId)
    {
        return set & ~(1L << elementId);
    }

    public static long getEmptySet()
    {
        return 0L;
    }

    public static long getFilledSet(int size)
    {
        if(size >= 64)
            return -1L;
        else
            return (1L << size) - 1L;
    }

    public static long getUnion(long set1, long set2)
    {
        return set1 | set2;
    }

    public static long getIntersection(long set1, long set2)
    {
        return set1 & set2;
    }

    public static boolean containsSubset(long set, long subset)
    {
        return (subset & set) == subset;
    }

    public static long removeSet(long set, long subset)
    {
        return set & ~subset;
    }

    public static final int MAX_SIZE = 64;
    private long setId;
}
