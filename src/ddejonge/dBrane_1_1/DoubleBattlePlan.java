// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DoubleBattlePlan.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.RegionSet;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package ddejonge.dBrane_1_1:
//            OrderContainer, BattlePlan

public class DoubleBattlePlan
    implements OrderContainer
{

    public DoubleBattlePlan(BattlePlan pp0, BattlePlan pp1)
    {
        this.pp0 = pp0;
        this.pp1 = pp1;
    }

    public List getAllOrders()
    {
        if(allOrders == null)
        {
            allOrders = new ArrayList(pp0.allOrders.size() + pp1.allOrders.size());
            allOrders.addAll(pp0.allOrders);
            allOrders.addAll(pp1.allOrders);
        }
        return allOrders;
    }

    public RegionSet getAllUnits()
    {
        if(allUnits == null)
        {
            allUnits = new RegionSet();
            allUnits.addAll(pp0.units);
            allUnits.addAll(pp1.units);
        }
        return allUnits;
    }

    public String toString()
    {
        return (new StringBuilder("{")).append(pp0.toString()).append(", ").append(pp1.toString()).append("}").toString();
    }

    BattlePlan pp0;
    BattlePlan pp1;
    private List allOrders;
    private RegionSet allUnits;
}
