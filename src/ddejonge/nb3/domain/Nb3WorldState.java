// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3WorldState.java

package ddejonge.nb3.domain;

import java.util.ArrayList;

public abstract class Nb3WorldState
{

    public Nb3WorldState()
    {
    }

    public abstract boolean isLegal(ArrayList arraylist);

    public abstract void update(ArrayList arraylist);

    public abstract Nb3WorldState copy();
}
