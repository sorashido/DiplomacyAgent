// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BattlePlanStorage.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.PowerSet;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            BattlePlan

class BattlePlanStorage
{

    private BattlePlanStorage()
    {
        storeByScAndCoalition = new List[34][128];
        storeByStringRep = new HashMap();
        plansOfOnePower = new List[34][7];
        plansHostileToPower = new List[34][7];
        emptyList = new ArrayList(0);
    }

    void addPlans(int scId, List partialPlans)
    {
        BattlePlan pp;
        for(Iterator iterator = partialPlans.iterator(); iterator.hasNext(); addPlan(scId, pp))
            pp = (BattlePlan)iterator.next();

    }

    void addPlan(int scId, BattlePlan partialPlan)
    {
        if(partialPlan.getPassivePowers().size() == 0)
        {
            if(partialPlan.getActivePowers().size() == 1)
            {
                if(plansOfOnePower[scId][partialPlan.getOnlyPower().getId()] == null)
                    plansOfOnePower[scId][partialPlan.getOnlyPower().getId()] = new ArrayList();
                plansOfOnePower[scId][partialPlan.getOnlyPower().getId()].add(partialPlan);
            }
            for(int i = 0; i < 7; i++)
                if(!partialPlan.getActivePowers().contains(i))
                {
                    if(plansHostileToPower[scId][i] == null)
                        plansHostileToPower[scId][i] = new ArrayList();
                    plansHostileToPower[scId][i].add(partialPlan);
                }

        }
        if(storeByScAndCoalition != null)
        {
            if(storeByScAndCoalition[scId][partialPlan.getCoalitionId()] == null)
                storeByScAndCoalition[scId][partialPlan.getCoalitionId()] = new ArrayList();
            storeByScAndCoalition[scId][partialPlan.getCoalitionId()].add(partialPlan);
            storeByStringRep.put(partialPlan.toString(), partialPlan);
        }
    }

    List getIndividualBattlePlans(int scId, Power power)
    {
        if(plansOfOnePower[scId][power.getId()] == null)
            return emptyList;
        else
            return plansOfOnePower[scId][power.getId()];
    }

    List getHostilePlans(int scId, Power power)
    {
        List toReturn = plansHostileToPower[scId][power.getId()];
        if(toReturn == null)
            return emptyList;
        else
            return toReturn;
    }

    List getPartialPlansIncludingPassivePowers(int scID, int coalitionId)
    {
        if(storeByScAndCoalition[scID][coalitionId] == null)
            return emptyList;
        else
            return storeByScAndCoalition[scID][coalitionId];
    }

    void remove(BattlePlan partialPlan)
    {
        int scId = partialPlan.getTargetProvince().getId();
        if(partialPlan.getPassivePowers().size() == 0)
        {
            if(partialPlan.getActivePowers().size() == 1)
                plansOfOnePower[scId][partialPlan.getOnlyPower().getId()].remove(partialPlan);
            for(int i = 0; i < 7; i++)
                if(!partialPlan.getActivePowers().contains(i))
                    plansHostileToPower[scId][i].remove(partialPlan);

        }
        if(storeByScAndCoalition != null)
            storeByScAndCoalition[scId][partialPlan.getCoalitionId()].remove(partialPlan);
    }

    private void makeCopyOf(BattlePlanStorage original)
    {
        for(int i = 0; i < original.plansOfOnePower.length; i++)
        {
            for(int j = 0; j < original.plansOfOnePower[i].length; j++)
            {
                if(original.plansOfOnePower[i][j] == null)
                    plansOfOnePower[i][j] = null;
                else
                if(plansOfOnePower[i][j] == null)
                {
                    plansOfOnePower[i][j] = new ArrayList(original.plansOfOnePower[i][j]);
                } else
                {
                    plansOfOnePower[i][j].clear();
                    plansOfOnePower[i][j].addAll(original.plansOfOnePower[i][j]);
                }
                if(original.plansHostileToPower[i][j] == null)
                    plansHostileToPower[i][j] = null;
                else
                if(plansHostileToPower[i][j] == null)
                {
                    plansHostileToPower[i][j] = new ArrayList(original.plansHostileToPower[i][j]);
                } else
                {
                    plansHostileToPower[i][j].clear();
                    plansHostileToPower[i][j].addAll(original.plansHostileToPower[i][j]);
                }
            }

        }

        storeByScAndCoalition = null;
        storeByStringRep = null;
    }

    static BattlePlanStorage getNew(BattlePlanStorage original)
    {
        if(recycleBin.size() == 0)
        {
            BattlePlanStorage partialPlanStorage = new BattlePlanStorage();
            partialPlanStorage.makeCopyOf(original);
            return partialPlanStorage;
        } else
        {
            BattlePlanStorage partialPlanStorage = (BattlePlanStorage)recycleBin.remove(recycleBin.size() - 1);
            partialPlanStorage.makeCopyOf(original);
            return partialPlanStorage;
        }
    }

    static BattlePlanStorage getNew()
    {
        return new BattlePlanStorage();
    }

    static void dispose(BattlePlanStorage partialPlanStorage)
    {
        if(partialPlanStorage == null)
            return;
        if(recycleBin.size() < 1000)
            recycleBin.add(partialPlanStorage);
    }

    BattlePlan getByStringRep(String stringRep)
    {
        return (BattlePlan)storeByStringRep.get(stringRep);
    }

    static final int CAPACITY = 1000;
    static ArrayList recycleBin = new ArrayList(1000);
    List storeByScAndCoalition[][];
    HashMap storeByStringRep;
    List plansOfOnePower[][];
    List plansHostileToPower[][];
    ArrayList emptyList;

}
