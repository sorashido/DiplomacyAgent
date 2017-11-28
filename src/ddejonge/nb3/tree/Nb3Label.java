// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Label.java

package ddejonge.nb3.tree;

import ddejonge.nb3.algorithm.Nb3Logger;

public class Nb3Label
{

    public Nb3Label()
    {
        type = 0;
        value = null;
    }

    public Nb3Label(int type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    public int getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }

    public String toString()
    {
        return "";
    }

    public void log(Nb3Logger nb3logger)
    {
    }

    public boolean canBeProposed()
    {
        return true;
    }

    public boolean equals(Nb3Label other)
    {
        return false;
    }

    protected int type;
    protected Object value;
}
