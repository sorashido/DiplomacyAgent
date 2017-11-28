// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3AgentSet_old.java

package ddejonge.nb3.algorithm;

import ddejonge.nb3.domain.Nb3InstanceInfo;

// Referenced classes of package ddejonge.nb3.algorithm:
//            Set

public class Nb3AgentSet_old extends Set
{

    public Nb3AgentSet_old()
    {
        super(Nb3InstanceInfo.numAgents);
    }

    public Nb3AgentSet_old(int num)
    {
        super(num);
    }

    public String toString()
    {
        String s = "{ ";
        for(int i = 0; i < getCapacity(); i++)
            if(contains(i))
                s = (new StringBuilder(String.valueOf(s))).append(Nb3InstanceInfo.agentNames[i]).append(", ").toString();

        int n = s.lastIndexOf(',');
        if(n >= 1)
            s = s.substring(0, n);
        s = (new StringBuilder(String.valueOf(s))).append(" }").toString();
        return s;
    }
}
