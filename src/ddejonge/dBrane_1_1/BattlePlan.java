// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BattlePlan.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.*;
import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.orders.*;
import java.io.Serializable;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            OrderContainer

class BattlePlan
    implements Comparable, Serializable, OrderContainer
{

    BattlePlan(int coalitionID, Province conflictArea)
    {
        hasUselessPowers = false;
        passivePowers = null;
        activePowers = null;
        onlyPower = null;
        isEmpty = false;
        mainOrder = null;
        supports = null;
        cuts = null;
        allOrders = new ArrayList();
        bounce = null;
        unit2itsOrder = new Order[120];
        units = new RegionSet();
        uncuttableForce = -1;
        stringRep = null;
        id = ppsCreated++;
        coalitionId = coalitionID;
        conflictAreaID = conflictArea.getId();
        targetProvince = conflictArea;
        isEmpty = true;
    }

    BattlePlan(int coalitionID, Order mainOrder, List supportsAndCuts)
    {
        hasUselessPowers = false;
        passivePowers = null;
        activePowers = null;
        onlyPower = null;
        isEmpty = false;
        this.mainOrder = null;
        supports = null;
        cuts = null;
        allOrders = new ArrayList();
        bounce = null;
        unit2itsOrder = new Order[120];
        units = new RegionSet();
        uncuttableForce = -1;
        stringRep = null;
        id = ppsCreated++;
        coalitionId = coalitionID;
        this.mainOrder = mainOrder;
        supports = new ArrayList();
        cuts = new ArrayList();
        if(mainOrder instanceof MTOOrder)
            targetProvince = ((MTOOrder)mainOrder).getDestination().getProvince();
        else
        if((mainOrder instanceof HLDOrder) || (mainOrder instanceof SUPOrder) || (mainOrder instanceof SUPMTOOrder))
            targetProvince = mainOrder.getLocation().getProvince();
        else
            throw new IllegalArgumentException((new StringBuilder("PartialPlan.getProvince() Error! Unknown order type: ")).append(mainOrder.getClass().getName()).toString());
        locationID0 = mainOrder.getLocation().getProvince().getId();
        locationID1 = -1;
        for(Iterator iterator = supportsAndCuts.iterator(); iterator.hasNext();)
        {
            Order order = (Order)iterator.next();
            if(order instanceof MTOOrder)
                cuts.add((MTOOrder)order);
            else
                supports.add(order);
        }

        addOrderToMap(mainOrder);
        addOrdersToMap(supports);
        addOrdersToMap(cuts);
        conflictAreaID = targetProvince.getId();
    }

    BattlePlan(int coalitionID, MTOOrder move0, MTOOrder move1)
    {
        hasUselessPowers = false;
        passivePowers = null;
        activePowers = null;
        onlyPower = null;
        isEmpty = false;
        mainOrder = null;
        supports = null;
        cuts = null;
        allOrders = new ArrayList();
        bounce = null;
        unit2itsOrder = new Order[120];
        units = new RegionSet();
        uncuttableForce = -1;
        stringRep = null;
        id = ppsCreated++;
        coalitionId = coalitionID;
        conflictAreaID = move0.getDestination().getProvince().getId();
        bounce = new MTOOrder[2];
        bounce[0] = move0;
        bounce[1] = move1;
        targetProvince = move0.getDestination().getProvince();
        locationID0 = move0.getLocation().getProvince().getId();
        locationID1 = move1.getLocation().getProvince().getId();
        supports = new ArrayList(1);
        cuts = new ArrayList(1);
        addOrderToMap(move0);
        addOrderToMap(move1);
    }

    void setIdPerSC(int idPerSc)
    {
        idPerSC = idPerSc;
    }

    int getIdPerSC()
    {
        return idPerSC;
    }

    Province getTargetProvince()
    {
        return targetProvince;
    }

    int getCoalitionId()
    {
        return coalitionId;
    }

    boolean isParticipating(Power power)
    {
        return PowerSet.contains(power.getId(), coalitionId);
    }

    boolean isParticipating(int powerID)
    {
        return PowerSet.contains(powerID, coalitionId);
    }

    Order getOrderOf(Region unit)
    {
        return unit2itsOrder[unit.getId()];
    }

    RegionSet getUnits()
    {
        return units;
    }

    int getSmallForce()
    {
        if(isEmpty)
            return 0;
        if(bounce != null)
            return 1;
        int cutStrength = cuts.size();
        if(cutStrength > 0)
            cutStrength--;
        return supports.size() + cutStrength + 1;
    }

    int getBigForce()
    {
        if(isEmpty)
            return 0;
        if(bounce != null)
            return 1;
        else
            return supports.size() + cuts.size() + 1;
    }

    int getUncuttableForce()
    {
        if(uncuttableForce == -1)
            throw new IllegalArgumentException("PartialPlan.getUncuttableForce() Error! uncuttable force for this partial plan has not yet been calculated!");
        else
            return uncuttableForce;
    }

    int getUncuttableForce(DiplomacyGame diplomacyGame, PowerSet opposingPowers)
    {
        if(uncuttableForce != -1)
            return uncuttableForce;
        RegionSet nextToTarget = diplomacyGame.getAdjacentUnits(targetProvince);
        RegionSet opponentSupporters = DipUtils.filterUnits(diplomacyGame, nextToTarget, opposingPowers);
        uncuttableForce = 0;
        if(supports != null)
        {
            for(Iterator iterator = supports.iterator(); iterator.hasNext();)
            {
                Order support = (Order)iterator.next();
                Province location = support.getLocation().getProvince();
                RegionSet nextToLocation = diplomacyGame.getAdjacentUnits(location);
                RegionSet opponentCutters = DipUtils.filterUnits(diplomacyGame, nextToLocation, opposingPowers);
                if(opponentCutters.size() <= opponentSupporters.size())
                {
                    boolean isSubset = true;
                    for(Iterator iterator1 = opponentCutters.iterator(); iterator1.hasNext();)
                    {
                        Region r = (Region)iterator1.next();
                        if(!opponentSupporters.contains(r))
                        {
                            isSubset = false;
                            break;
                        }
                    }

                    if(isSubset)
                        uncuttableForce++;
                }
            }

        }
        if(mainOrder != null)
            uncuttableForce++;
        return uncuttableForce;
    }

    private void addOrdersToMap(List orders)
    {
        Order order;
        for(Iterator iterator = orders.iterator(); iterator.hasNext(); addOrderToMap(order))
            order = (Order)iterator.next();

    }

    private void addOrderToMap(Order order)
    {
        unit2itsOrder[order.getLocation().getId()] = order;
        units.add(order.getLocation());
        allOrders.add(order);
        if(order instanceof SUPOrder)
        {
            Order supportedOrder = ((SUPOrder)order).getSupportedOrder();
            Region supportedUnit = supportedOrder.getLocation();
            units.add(supportedUnit);
            unit2itsOrder[supportedUnit.getId()] = supportedOrder;
        } else
        if(order instanceof SUPMTOOrder)
        {
            Order supportedOrder = ((SUPMTOOrder)order).getSupportedOrder();
            Region supportedUnit = supportedOrder.getLocation();
            units.add(supportedUnit);
            unit2itsOrder[supportedUnit.getId()] = supportedOrder;
        }
    }

    static Power getLeadingPower(BattlePlan partialPlan, DiplomacyGame diplomacyGame)
    {
        if(partialPlan.mainOrder == null)
        {
            Power owner = diplomacyGame.getOwner(partialPlan.getTargetProvince());
            return owner;
        } else
        {
            return partialPlan.mainOrder.getPower();
        }
    }

    PowerSet getPassivePowers()
    {
        if(passivePowers == null)
        {
            passivePowers = new PowerSet(coalitionId);
            Order order;
            for(Iterator iterator = allOrders.iterator(); iterator.hasNext(); passivePowers.remove(order.getPower()))
                order = (Order)iterator.next();

        }
        return passivePowers;
    }

    PowerSet getActivePowers()
    {
        if(activePowers == null)
        {
            activePowers = new PowerSet();
            Order order;
            for(Iterator iterator = allOrders.iterator(); iterator.hasNext(); activePowers.add(order.getPower()))
                order = (Order)iterator.next();

        }
        return activePowers;
    }

    Power getOnlyPower()
    {
        if(getActivePowers().size() != 1)
            return null;
        if(onlyPower == null)
        {
            for(Iterator iterator = activePowers.iterator(); iterator.hasNext();)
            {
                int powerID = ((Integer)iterator.next()).intValue();
                if(activePowers.contains(powerID))
                    onlyPower = DiplomacyGame.getPower(powerID);
            }

        }
        return onlyPower;
    }

    public String toString()
    {
        if(stringRep == null)
        {
            ArrayList copy_of_allorders = new ArrayList(allOrders);
            Collections.sort(copy_of_allorders);
            stringRep = copy_of_allorders.toString();
            stringRep = (new StringBuilder(String.valueOf(DiplomacyGame.getProvince(conflictAreaID).getName()))).append("-").append(coalitionId).append("-").append(stringRep).toString();
        }
        return stringRep;
    }

    boolean isEmpty()
    {
        return isEmpty;
    }

    public int compareTo(BattlePlan pp)
    {
        return id - pp.id;
    }

    public List getAllOrders()
    {
        return allOrders;
    }

    public RegionSet getAllUnits()
    {
        return units;
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((BattlePlan)obj);
    }

    static int ppsCreated = 0;
    int id;
    int idPerSC;
    boolean hasUselessPowers;
    private PowerSet passivePowers;
    private PowerSet activePowers;
    private Power onlyPower;
    private Province targetProvince;
    int locationID0;
    int locationID1;
    int coalitionId;
    int conflictAreaID;
    boolean isEmpty;
    Order mainOrder;
    List supports;
    List cuts;
    List allOrders;
    MTOOrder bounce[];
    private Order unit2itsOrder[];
    RegionSet units;
    int uncuttableForce;
    private String stringRep;

}
