// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ArrayOfLists.java

package ddejonge.dBrane.tools;

import java.util.ArrayList;

public class ArrayOfLists
{

    public ArrayOfLists(int arraySize, int defaultListSize, boolean autoResize)
    {
        this.autoResize = false;
        array = new Object[arraySize];
        this.defaultListSize = defaultListSize;
        this.autoResize = autoResize;
    }

    public ArrayOfLists(int arraySize, int defaultListSize)
    {
        autoResize = false;
        array = new Object[arraySize];
        this.defaultListSize = defaultListSize;
    }

    public void add(int index, Object object)
    {
        if(index >= array.length && autoResize)
        {
            Object newArray[] = new Object[index + 1];
            for(int i = 0; i < array.length; i++)
                newArray[i] = array[i];

            array = newArray;
        }
        if(array[index] == null)
            array[index] = new ArrayList(defaultListSize);
        ((ArrayList)array[index]).add(object);
    }

    public Object get(int row, int col)
    {
        return ((ArrayList)array[row]).get(col);
    }

    public Object remove(int row, int col)
    {
        if(array[row] == null || ((ArrayList)array[row]).size() == 0)
            return null;
        else
            return ((ArrayList)array[row]).remove(col);
    }

    public ArrayList get(int row)
    {
        return (ArrayList)array[row];
    }

    public void clear()
    {
        for(int i = 0; i < array.length; i++)
            if(array[i] != null)
                ((ArrayList)array[i]).clear();

    }

    public int getListSize(int index)
    {
        if(array[index] == null)
            return 0;
        else
            return ((ArrayList)array[index]).size();
    }

    public int getArraySize()
    {
        return array.length;
    }

    Object array[];
    int defaultListSize;
    boolean autoResize;
}
