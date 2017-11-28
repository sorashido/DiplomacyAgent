// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderContainer.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.RegionSet;
import java.util.List;

public interface OrderContainer
{

    public abstract List getAllOrders();

    public abstract RegionSet getAllUnits();
}
