// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DipUtils.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.orders.*;
import java.util.*;

// Referenced classes of package ddejonge.dipgameExtensions:
//            RegionSet, DiplomacyGame, PowerSet

public class DipUtils
{

    public DipUtils()
    {
    }

    public static boolean areEqual(Order order1, Order order2)
    {
        if(order1.getLocation().getId() != order2.getLocation().getId())
            return false;
        if(order1 instanceof HLDOrder)
            return order2 instanceof HLDOrder;
        if(order1 instanceof MTOOrder)
            if(order2 instanceof MTOOrder)
                return ((MTOOrder)order1).getDestination().getId() == ((MTOOrder)order2).getDestination().getId();
            else
                return false;
        if(order1 instanceof SUPMTOOrder)
            if(order2 instanceof SUPMTOOrder)
                return areEqual(((Order) (((SUPMTOOrder)order1).getSupportedOrder())), ((Order) (((SUPMTOOrder)order2).getSupportedOrder())));
            else
                return false;
        if(order1 instanceof SUPOrder)
        {
            if(order2 instanceof SUPOrder)
            {
                Order supportedOrder1 = ((SUPOrder)order1).getSupportedOrder();
                Order supportedOrder2 = ((SUPOrder)order2).getSupportedOrder();
                return supportedOrder1.getLocation().getId() == supportedOrder2.getLocation().getId();
            } else
            {
                return false;
            }
        } else
        {
            throw new IllegalArgumentException((new StringBuilder("OrderComparer.areEqual() Error! unknown order type: ")).append(order1.getClass().getName()).toString());
        }
    }

    public static boolean areConsistent(List orders)
    {
        for(int j = 0; j < orders.size(); j++)
        {
            for(int k = j + 1; k < orders.size(); k++)
            {
                Order order1 = (Order)orders.get(j);
                Order order2 = (Order)orders.get(k);
                if(order1.getLocation().getId() == order2.getLocation().getId())
                    return false;
                if((order1 instanceof MTOOrder) && (order2 instanceof MTOOrder) && ((MTOOrder)order1).getDestination().getProvince().getId() == ((MTOOrder)order2).getDestination().getProvince().getId())
                    return false;
            }

        }

        return true;
    }

    public static RegionSet filterUnits(DiplomacyGame diplomacyGame, RegionSet units, PowerSet powers)
    {
        RegionSet filteredUnits = new RegionSet();
        for(Iterator iterator = units.iterator(); iterator.hasNext();)
        {
            Region unit = (Region)iterator.next();
            if(powers.contains(diplomacyGame.getController(unit)))
                filteredUnits.add(unit);
        }

        return filteredUnits;
    }

    public static RegionSet filterUnits(DiplomacyGame diplomacyGame, RegionSet units, Power power)
    {
        RegionSet filteredUnits = new RegionSet();
        for(Iterator iterator = units.iterator(); iterator.hasNext();)
        {
            Region unit = (Region)iterator.next();
            if(power.getId() == diplomacyGame.getController(unit).getId())
                filteredUnits.add(unit);
        }

        return filteredUnits;
    }

    public static ArrayList cleanUp(Game game, Power power, List orders)
    {
        HashMap units2orders = new HashMap();
        for(int i = 0; i < orders.size(); i++)
        {
            Order order = (Order)orders.get(i);
            if(order.getPower().getName().equals(power.getName()))
            {
                String unitLocation = order.getLocation().getName();
                Power controller = game.getController(order.getLocation());
                if(controller.getName().equals(power.getName()))
                    throw new IllegalArgumentException((new StringBuilder("Error! The list contains the order ")).append(order).append(" but ").append(power.getName()).append(" does not have a unit at ").append(order.getLocation().getName()).toString());
                Order existingOrder = (Order)units2orders.get(unitLocation);
                if(existingOrder == null)
                    units2orders.put(unitLocation, order);
                else
                if(!existingOrder.equals(order))
                    if(existingOrder instanceof HLDOrder)
                    {
                        if((order instanceof SUPMTOOrder) || (order instanceof SUPOrder))
                            units2orders.put(unitLocation, order);
                        else
                        if(!(order instanceof HLDOrder))
                            throw new IllegalArgumentException((new StringBuilder("Error! The list contains inconsistent orders: ")).append(order).append(" and ").append(existingOrder).toString());
                    } else
                    if((existingOrder instanceof SUPMTOOrder) || (existingOrder instanceof SUPOrder))
                    {
                        if(!(order instanceof HLDOrder))
                            throw new IllegalArgumentException((new StringBuilder("Error! The list contains inconsistent orders: ")).append(order).append(" and ").append(existingOrder).toString());
                    } else
                    {
                        throw new IllegalArgumentException((new StringBuilder("Error! The list contains inconsistent orders: ")).append(order).append(" and ").append(existingOrder).toString());
                    }
            }
        }

        return new ArrayList(units2orders.values());
    }

    public static ArrayList addHoldOrders(Power power, List orders)
    {
        List units = power.getControlledRegions();
        ArrayList newList = new ArrayList(units.size());
        newList.addAll(orders);
        for(Iterator iterator = units.iterator(); iterator.hasNext();)
        {
            Region unit = (Region)iterator.next();
            boolean hasOrder = false;
            for(Iterator iterator1 = orders.iterator(); iterator1.hasNext();)
            {
                Order order = (Order)iterator1.next();
                if(order.getLocation().getName().equals(unit.getName()))
                {
                    hasOrder = true;
                    break;
                }
            }

            if(!hasOrder)
                newList.add(new HLDOrder(power, unit));
        }

        return newList;
    }
}
