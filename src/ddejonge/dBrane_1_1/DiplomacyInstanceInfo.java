// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DiplomacyInstanceInfo.java

package ddejonge.dBrane_1_1;

import ddejonge.nb3.domain.Nb3InstanceInfo;
import es.csic.iiia.fabregues.dip.board.Power;
import java.util.*;

public class DiplomacyInstanceInfo extends Nb3InstanceInfo
{

    public DiplomacyInstanceInfo()
    {
    }

    public static void initializeAgentNames(List powers)
    {
        numAgents = 7;
        agentNames = new String[numAgents];
        if(powers.size() != numAgents)
            throw new IllegalArgumentException((new StringBuilder("DiplomacyInstanceInfo.initializeAgentNames() Error! number of agents is: ")).append(numAgents).append(" number of powers is: ").append(powers.size()).toString());
        for(Iterator iterator = powers.iterator(); iterator.hasNext();)
        {
            Power power = (Power)iterator.next();
            agentNames[power.getId()] = power.getName();
        }

        _name2int = new HashMap();
        for(int i = 0; i < agentNames.length; i++)
            _name2int.put(agentNames[i], Integer.valueOf(i));

    }
}
