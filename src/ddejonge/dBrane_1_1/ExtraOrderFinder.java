// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ExtraOrderFinder.java

package ddejonge.dBrane_1_1;

import ddejonge.dBrane.tools.ArrayOfLists;
import ddejonge.dipgameExtensions.*;
import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.orders.*;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            CoalitionStructure, BattlePlan

public class ExtraOrderFinder
{

    public ExtraOrderFinder()
    {
    }

    public static void addExtraOrders(DiplomacyGame diplomacyGame, ArrayList myOrders, CoalitionStructure cs, List confirmedDeals, boolean obeyImplicitAgreements)
    {
        unitsWithoutOrder.clear();
        unitsWithoutOrder.addAll(diplomacyGame.getUnitsOf(cs.getMe()));
        Order order;
        for(Iterator iterator = myOrders.iterator(); iterator.hasNext(); unitsWithoutOrder.remove(order.getLocation()))
            order = (Order)iterator.next();

        int province2RequiredReinforcement[] = getRequiredReinforcements(diplomacyGame, cs);
        ArrayOfLists targets = new ArrayOfLists(20, 10);
        for(Iterator iterator1 = DiplomacyGame.getSupplyCenters().iterator(); iterator1.hasNext();)
        {
            Province sc = (Province)iterator1.next();
            if(province2RequiredReinforcement[sc.getId()] > 0)
                targets.add(province2RequiredReinforcement[sc.getId()], sc);
        }

        ProvinceSet forbiddenTargets = new ProvinceSet();
        BattlePlan pp;
        for(Iterator iterator2 = confirmedDeals.iterator(); iterator2.hasNext(); forbiddenTargets.add(pp.getTargetProvince()))
            pp = (BattlePlan)iterator2.next();

        if(obeyImplicitAgreements)
        {
            for(Iterator iterator3 = DiplomacyGame.getSupplyCenters().iterator(); iterator3.hasNext();)
            {
                Province sc = (Province)iterator3.next();
                if(diplomacyGame.getOwner(sc) != null && diplomacyGame.getOwner(sc).getId() != cs.getMe().getId() && cs.getAllies().contains(diplomacyGame.getOwner(sc)))
                    forbiddenTargets.add(sc);
            }

        }
        ProvinceSet forbiddenProvinces = new ProvinceSet();
        forbiddenProvinces.add(forbiddenTargets);
        for(int provId = 0; provId < 75; provId++)
            if(diplomacyGame.getController(provId) != null)
                forbiddenProvinces.add(provId);

        for(Iterator iterator4 = myOrders.iterator(); iterator4.hasNext();)
        {
            Order o = (Order)iterator4.next();
            if(o instanceof MTOOrder)
                forbiddenProvinces.add(((MTOOrder)o).getDestination().getProvince());
        }

        ArrayOfLists sc2team = getReinforcementTeams(province2RequiredReinforcement, unitsWithoutOrder);
        int province2reinforcementTime[] = getReinforcementTime(sc2team);
        ArrayOfLists reinforcementTime2Provinces = new ArrayOfLists(10, 10, true);
        Province sc;
        for(Iterator iterator5 = DiplomacyGame.getSupplyCenters().iterator(); iterator5.hasNext(); reinforcementTime2Provinces.add(province2reinforcementTime[sc.getId()], sc))
            sc = (Province)iterator5.next();

        List newOrders = new ArrayList();
        ArrayList unitsCloseToThisSC = new ArrayList();
        for(int mVal = 0; mVal < 10 && unitsWithoutOrder.size() != 0;)
        {
            Province sc;
            if(reinforcementTime2Provinces.get(mVal) != null && reinforcementTime2Provinces.get(mVal).size() != 0)
                sc = (Province)reinforcementTime2Provinces.remove(mVal, 0);
            else
                sc = null;
            if(sc == null)
                mVal++;
            else
            if(!forbiddenTargets.contains(sc))
            {
                unitsCloseToThisSC.clear();
                if(sc2team.get(sc.getId()) != null)
                {
                    for(Iterator iterator6 = sc2team.get(sc.getId()).iterator(); iterator6.hasNext();)
                    {
                        Region reg = (Region)iterator6.next();
                        if(unitsWithoutOrder.contains(reg))
                            unitsCloseToThisSC.add(reg);
                    }

                }
                List newOrdersForThisSC = moveUnitsToTarget(cs.getMe(), unitsCloseToThisSC, sc, forbiddenProvinces);
                if(newOrdersForThisSC != null)
                {
                    for(Iterator iterator8 = newOrdersForThisSC.iterator(); iterator8.hasNext();)
                    {
                        Order o = (Order)iterator8.next();
                        unitsWithoutOrder.remove(o.getLocation());
                        if(o instanceof MTOOrder)
                            forbiddenProvinces.add(((MTOOrder)o).getDestination().getProvince());
                        else
                            forbiddenProvinces.add(o.getLocation().getProvince());
                    }

                    newOrders.addAll(newOrdersForThisSC);
                }
            }
        }

        Region unit;
        for(Iterator iterator7 = unitsWithoutOrder.iterator(); iterator7.hasNext(); newOrders.add(new HLDOrder(cs.getMe(), unit)))
            unit = (Region)iterator7.next();

        myOrders.addAll(newOrders);
    }

    static int[] getRequiredReinforcements(DiplomacyGame diplomacyGame, CoalitionStructure cs)
    {
        Arrays.fill(province2RequiredReinforcement, 0);
        PowerSet opponents = cs.getAllies().complement();
        for(Iterator iterator = DiplomacyGame.getSupplyCenters().iterator(); iterator.hasNext();)
        {
            Province sc = (Province)iterator.next();
            Power owner = diplomacyGame.getOwner(sc);
            Power controller = diplomacyGame.getController(sc);
            int n = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), opponents).size();
            if(controller != null && opponents.contains(controller))
                n++;
            int k = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), cs.getAllies()).size();
            if(controller != null && cs.getAllies().contains(controller))
                k++;
            int requiredReinforcement = n - k;
            if(owner == null || opponents.contains(owner))
                requiredReinforcement++;
            province2RequiredReinforcement[sc.getId()] = requiredReinforcement;
        }

        return province2RequiredReinforcement;
    }

    static ArrayOfLists getReinforcementTeams(int province2RequiredReinforcement[], RegionSet availableUnits)
    {
        reinforcementTeams.clear();
        for(Iterator iterator = DiplomacyGame.getSupplyCenters().iterator(); iterator.hasNext();)
        {
            Province sc = (Province)iterator.next();
            _availableUnits.clear();
            _availableUnits.addAll(availableUnits);
            int r = province2RequiredReinforcement[sc.getId()];
            if(r <= availableUnits.size())
            {
                for(int j = 0; j < r; j++)
                {
                    Region closeUnit = getUnitClosestToSC(sc, _availableUnits);
                    if(closeUnit == null)
                        break;
                    _availableUnits.remove(closeUnit);
                    reinforcementTeams.add(sc.getId(), closeUnit);
                }

            }
        }

        return reinforcementTeams;
    }

    static int[] getReinforcementTime(ArrayOfLists reinforcementTeams)
    {
        Arrays.fill(province2mValue, 9);
        for(Iterator iterator = DiplomacyGame.getSupplyCenters().iterator(); iterator.hasNext();)
        {
            Province sc = (Province)iterator.next();
            int m = 0;
            ArrayList unitsCloseToThisSC = reinforcementTeams.get(sc.getId());
            if(unitsCloseToThisSC != null)
            {
                for(Iterator iterator1 = unitsCloseToThisSC.iterator(); iterator1.hasNext();)
                {
                    Region unit = (Region)iterator1.next();
                    int m_i = DiplomacyGame.getDistance(sc, unit);
                    if(m_i > m)
                        m = m_i;
                }

                province2mValue[sc.getId()] = m;
            }
        }

        return province2mValue;
    }

    public static Region getUnitClosestToSC(Province sc, RegionSet units)
    {
        int minDist = 1000;
        Region closest = null;
        for(Iterator iterator = units.iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            if(DiplomacyGame.getDistance(sc, r) < minDist)
            {
                closest = r;
                minDist = DiplomacyGame.getDistance(sc, r);
            }
        }

        return closest;
    }

    public static List moveUnitsToTarget(Power me, List units, Province target, ProvinceSet occupiedProvinces)
    {
        units2CloserNeighbors.clear();
        for(Iterator iterator = units.iterator(); iterator.hasNext();)
        {
            Region unit = (Region)iterator.next();
            for(Iterator iterator1 = DiplomacyGame.getNeighbors(unit).iterator(); iterator1.hasNext();)
            {
                Region neighbor = (Region)iterator1.next();
                if(DiplomacyGame.getDistance(target, neighbor) < DiplomacyGame.getDistance(target, unit) && !occupiedProvinces.contains(neighbor.getProvince().getId()))
                    units2CloserNeighbors.add(unit.getId(), neighbor);
            }

        }

        List newOrders = new ArrayList(units.size());
        Region destinations[] = new Region[units.size()];
        boolean success = addDestination(destinations, units, 0);
        if(!success)
            return null;
        for(int i = 0; i < destinations.length; i++)
        {
            Region r = destinations[i];
            newOrders.add(new MTOOrder(me, (Region)units.get(i), r));
        }

        return newOrders;
    }

    static boolean addDestination(Region destinations[], List units, int depth)
    {
        if(depth == destinations.length)
            return true;
        Region unit = (Region)units.get(depth);
        List possibleDestinations = units2CloserNeighbors.get(unit.getId());
        if(possibleDestinations == null)
            return false;
        for(int i = 0; i < possibleDestinations.size(); i++)
        {
            boolean thisRegionIsAlreadyChosen = false;
            for(int j = 0; j < depth; j++)
            {
                Region r = destinations[j];
                if(r == null || r.getProvince().getId() != ((Region)possibleDestinations.get(i)).getProvince().getId())
                    continue;
                thisRegionIsAlreadyChosen = true;
                break;
            }

            if(!thisRegionIsAlreadyChosen)
            {
                destinations[depth] = (Region)possibleDestinations.get(i);
                if(!DiplomacyGame.getNeighbors(destinations[depth]).contains(unit))
                    throw new IllegalArgumentException((new StringBuilder("DBraneCore_0_3.addDestination() Error! these two regions are not neighbors: ")).append(destinations[depth]).append(" ").append(unit).append("\nunits2CloserNeighbors: ").append(units2CloserNeighbors.get(unit.getId()).toString()).toString());
                if(!DiplomacyGame.getNeighbors(destinations[depth]).contains((Region)units.get(depth)))
                    throw new IllegalArgumentException((new StringBuilder("DBraneCore_0_3.addDestination() Error! these two regions are not neighbors: ")).append(destinations[depth]).append(" ").append(units.get(depth)).append("\nunits2CloserNeighbors: ").append(units2CloserNeighbors.get(unit.getId()).toString()).toString());
                if(addDestination(destinations, units, depth + 1))
                    return true;
            }
        }

        return false;
    }

    static RegionSet unitsWithoutOrder = new RegionSet();
    private static int province2RequiredReinforcement[] = new int[75];
    private static ArrayOfLists reinforcementTeams = new ArrayOfLists(34, 10);
    private static RegionSet _availableUnits = new RegionSet();
    private static int province2mValue[] = new int[34];
    private static ArrayOfLists units2CloserNeighbors = new ArrayOfLists(120, 10);

}
