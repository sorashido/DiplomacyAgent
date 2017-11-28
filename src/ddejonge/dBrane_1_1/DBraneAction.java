// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DBraneAction.java

package ddejonge.dBrane_1_1;

import ddejonge.nb3.algorithm.Nb3AgentSet;
import ddejonge.nb3.domain.Nb3Action;
import ddejonge.nb3.tools.LongSet;
import java.io.Serializable;

// Referenced classes of package ddejonge.dBrane_1_1:
//            BattlePlanStorage, BattlePlan

public class DBraneAction extends Nb3Action
    implements Comparable, Serializable
{

    public DBraneAction(BattlePlan partialPlan)
    {
        this.partialPlan = partialPlan;
        setPA();
    }

    public DBraneAction(String stringRep, BattlePlanStorage partialPlanStorage)
    {
        partialPlan = partialPlanStorage.getByStringRep(stringRep);
        this.stringRep = stringRep;
        if(partialPlan != null)
            setPA();
    }

    protected void setPA()
    {
        participatingAgents = new Nb3AgentSet();
        for(int i = 0; i < 7; i++)
            if(LongSet.containsElement(partialPlan.getCoalitionId(), i))
                participatingAgents.add(i);

    }

    public BattlePlan getPartialPlan()
    {
        return partialPlan;
    }

    public String toString()
    {
        if(stringRep == null)
            stringRep = partialPlan.toString();
        return stringRep;
    }

    public int compareTo(DBraneAction o)
    {
        return toString().compareTo(o.toString());
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((DBraneAction)obj);
    }

    BattlePlan partialPlan;
    String stringRep;
}
