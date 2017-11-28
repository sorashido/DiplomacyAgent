// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PlanListWithValue.java

package ddejonge.dBrane_1_1;

import java.util.ArrayList;
import java.util.List;

public class PlanListWithValue
{

    public PlanListWithValue()
    {
        plans = new ArrayList(0);
        value = -1000;
        securedSCs = 0L;
        id = numCreated++;
    }

    public PlanListWithValue(List plans, int value, long securedSCs)
    {
        this.plans = new ArrayList(plans);
        this.value = value;
        this.securedSCs = securedSCs;
        id = numCreated++;
    }

    public int getValue()
    {
        return value;
    }

    public List getPlans()
    {
        return plans;
    }

    public long getSecuredSCs()
    {
        return securedSCs;
    }

    public static int numCreated = 0;
    public int id;
    private List plans;
    int value;
    long securedSCs;

}
