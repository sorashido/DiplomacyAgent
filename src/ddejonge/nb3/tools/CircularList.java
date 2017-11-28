// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CircularList.java

package ddejonge.nb3.tools;

import java.io.PrintStream;

public class CircularList
{

    public CircularList()
    {
        list = new String[50];
        cursor = 0;
        size = 0;
    }

    public static void main(String args[])
    {
        CircularList cl = new CircularList();
        for(int i = 0; i < 22; i++)
            cl.add((new StringBuilder()).append(i).toString());

        System.out.println(cl);
    }

    public void add(String s)
    {
        list[cursor++] = s;
        cursor %= list.length;
        if(size < 50)
            size++;
    }

    public String toString()
    {
        String s = "";
        for(int i = 0; i < 50; i++)
        {
            int c;
            if(size < 50)
                c = i;
            else
                c = (cursor + i) % 50;
            if(list[c] == null)
                break;
            s = (new StringBuilder(String.valueOf(s))).append(list[c]).append("\n").toString();
        }

        return s;
    }

    final int CAPACITY = 50;
    String list[];
    int cursor;
    int size;
}
