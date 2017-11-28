// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Variable.java

package ddejonge.dBrane_1_1.andOrSearch;

import java.util.ArrayList;
import java.util.List;

public class Variable
{

    Variable(int id)
    {
        neighbors = new ArrayList();
        variableId = id;
    }

    List getNeighbors()
    {
        return neighbors;
    }

    public int getId()
    {
        return variableId;
    }

    public String toString()
    {
        return (new StringBuilder()).append(variableId).toString();
    }

    private int variableId;
    List neighbors;
}
