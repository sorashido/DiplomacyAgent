// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ForceCalculator.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.*;
import ddejonge.nb3.tools.LongSet;
import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package ddejonge.dBrane_1_1:
//            BattlePlan, BattlePlanStorage

class ForceCalculator
{

    ForceCalculator()
    {
    }

    static boolean isTooStrong(DiplomacyGame diplomacyGame, BattlePlan pp, BattlePlanStorage partialPlanStorage, PowerSet subCoalition, PowerSet opponents, boolean attack, boolean targetIsOccupied)
    {
        Province targetProvince = pp.getTargetProvince();
        int ourMinForce = pp.getUncuttableForce(diplomacyGame, opponents);
        int numCuttableSupports = pp.supports.size() - (ourMinForce - 1);
        usefulCuts.clear();
        Power power = null;
        for(int i = 0; i < 7; i++)
        {
            if(!subCoalition.contains(i))
                continue;
            power = DiplomacyGame.getPower(i);
            break;
        }

        int oppMaxForce = 0;
        List hostilePlans = partialPlanStorage.getHostilePlans(targetProvince.getId(), power);
        if(hostilePlans != null)
        {
            for(Iterator iterator = hostilePlans.iterator(); iterator.hasNext();)
            {
                BattlePlan oppPP = (BattlePlan)iterator.next();
                if(LongSet.containsSubset(opponents.getId(), oppPP.getCoalitionId()))
                {
                    int oppForce = 1;
                    if(oppPP.supports != null)
                    {
                        for(Iterator iterator1 = oppPP.supports.iterator(); iterator1.hasNext();)
                        {
                            Order o = (Order)iterator1.next();
                            boolean thisSupportIsCut = false;
                            for(Iterator iterator3 = pp.cuts.iterator(); iterator3.hasNext();)
                            {
                                MTOOrder cut = (MTOOrder)iterator3.next();
                                if(cut.getDestination().getProvince().getId() == o.getLocation().getProvince().getId())
                                {
                                    thisSupportIsCut = true;
                                    if(attack && 1 + oppPP.supports.size() >= ourMinForce || !attack && 1 + oppPP.supports.size() > ourMinForce)
                                        usefulCuts.add(cut.getLocation());
                                    break;
                                }
                            }

                            if(!thisSupportIsCut)
                                oppForce++;
                        }

                        if(oppForce > oppMaxForce)
                            oppMaxForce = oppForce;
                    }
                }
            }

        }
        if(pp.mainOrder != null && (pp.mainOrder instanceof MTOOrder))
        {
            Province currentLocation = pp.mainOrder.getLocation().getProvince();
            if(currentLocation.isSC())
            {
                BattlePlan h2hPlan = null;
                List potentialH2HPlans = partialPlanStorage.getHostilePlans(currentLocation.getId(), power);
                if(potentialH2HPlans != null)
                {
                    for(Iterator iterator2 = potentialH2HPlans.iterator(); iterator2.hasNext();)
                    {
                        BattlePlan pt = (BattlePlan)iterator2.next();
                        if(LongSet.containsSubset(opponents.getId(), pt.getCoalitionId()) && pt.mainOrder != null && pt.mainOrder.getLocation().getProvince().getId() == targetProvince.getId())
                        {
                            h2hPlan = pt;
                            break;
                        }
                    }

                    if(h2hPlan != null && h2hPlan.supports.size() + 1 > oppMaxForce)
                        oppMaxForce = h2hPlan.supports.size() + 1;
                }
            }
        }
        Power owner = diplomacyGame.getOwner(targetProvince);
        int maxRequiredForce;
        if(attack)
        {
            if(oppMaxForce == 0 && targetIsOccupied)
                oppMaxForce++;
            maxRequiredForce = oppMaxForce + 1;
        } else
        if(oppMaxForce == 0 && (owner == null || !subCoalition.contains(owner)))
            maxRequiredForce = 1;
        else
            maxRequiredForce = oppMaxForce;
        if(ourMinForce > maxRequiredForce)
            return true;
        return ourMinForce == maxRequiredForce && (numCuttableSupports > 0 || usefulCuts.size() < pp.cuts.size());
    }

    static RegionSet usefulCuts = new RegionSet();

}
