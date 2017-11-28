// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CompatibilityChecker.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.DipUtils;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.*;
import java.util.*;

class CompatibilityChecker
{

    CompatibilityChecker()
    {
    }

    static boolean determineCompatibility(List orders1, List orders2)
    {
        Arrays.fill(orderMap, null);
        for(Iterator iterator = orders1.iterator(); iterator.hasNext();)
        {
            Order order1 = (Order)iterator.next();
            int locId = order1.getLocation().getId();
            if(orderMap[locId] == null || !(order1 instanceof HLDOrder))
                orderMap[locId] = order1;
        }

        Arrays.fill(replacedHLDOrders, null);
        Iterator iterator1 = orders2.iterator();
        while(iterator1.hasNext()) 
        {
            Order o2 = (Order)iterator1.next();
            Order o1 = orderMap[o2.getLocation().getId()];
            if(o1 == null)
                continue;
            if((o1 instanceof MTOOrder) || (o2 instanceof MTOOrder))
            {
                if(!(o1 instanceof MTOOrder) || !(o2 instanceof MTOOrder))
                    return false;
                if(((MTOOrder)o1).getDestination().getId() != ((MTOOrder)o2).getDestination().getId())
                    return false;
                continue;
            }
            if(!(o1 instanceof HLDOrder) && !(o2 instanceof HLDOrder))
            {
                if(!areCompatible(o1, o2))
                    return false;
                continue;
            }
            if((o1 instanceof HLDOrder) && (o2 instanceof HLDOrder))
                continue;
            if(o1 instanceof HLDOrder)
                if(replacedHLDOrders[o1.getLocation().getId()] == null)
                {
                    replacedHLDOrders[o1.getLocation().getId()] = o2;
                } else
                {
                    if(!areCompatible(replacedHLDOrders[o1.getLocation().getId()], o2))
                        return false;
                    continue;
                }
            if(o2 instanceof HLDOrder)
                if(replacedHLDOrders[o2.getLocation().getId()] == null)
                    replacedHLDOrders[o2.getLocation().getId()] = o1;
                else
                if(!areCompatible(replacedHLDOrders[o2.getLocation().getId()], o1))
                    return false;
        }
        return true;
    }

    static boolean determineCompatibility(List orders)
    {
        Arrays.fill(orderMap, null);
        Arrays.fill(replacedHLDOrders, null);
        Iterator iterator = orders.iterator();
        while(iterator.hasNext()) 
        {
            Order order = (Order)iterator.next();
            if(order == null)
                throw new IllegalArgumentException((new StringBuilder("IllegalPlanTable.determineCompatibility() Error! one of the orders is null: ")).append(orders).toString());
            Order oldOrder = orderMap[order.getLocation().getId()];
            if(oldOrder == null || DipUtils.areEqual(oldOrder, order))
            {
                orderMap[order.getLocation().getId()] = order;
                continue;
            }
            if((oldOrder instanceof MTOOrder) || (order instanceof MTOOrder))
                return false;
            if(!(oldOrder instanceof HLDOrder) && !(order instanceof HLDOrder))
            {
                if(!areCompatible(oldOrder, order))
                    return false;
                continue;
            }
            if((oldOrder instanceof HLDOrder) && (order instanceof HLDOrder))
                continue;
            if(oldOrder instanceof HLDOrder)
                if(replacedHLDOrders[oldOrder.getLocation().getId()] == null)
                {
                    replacedHLDOrders[oldOrder.getLocation().getId()] = order;
                } else
                {
                    if(!areCompatible(replacedHLDOrders[oldOrder.getLocation().getId()], order))
                        return false;
                    continue;
                }
            if(order instanceof HLDOrder)
                if(replacedHLDOrders[order.getLocation().getId()] == null)
                    replacedHLDOrders[order.getLocation().getId()] = oldOrder;
                else
                if(!areCompatible(replacedHLDOrders[order.getLocation().getId()], oldOrder))
                    return false;
        }
        return true;
    }

    static boolean hasNoSelfCuts(List branchOrders, List childOrders)
    {
        int brSize = branchOrders.size();
        int chldSize = childOrders.size();
        Arrays.fill(location2order, null);
        for(int i = 0; i < brSize; i++)
            location2order[((Order)branchOrders.get(i)).getLocation().getProvince().getId()] = (Order)branchOrders.get(i);

        for(int j = 0; j < chldSize; j++)
            location2order[((Order)childOrders.get(j)).getLocation().getProvince().getId()] = (Order)childOrders.get(j);

        for(int j = 0; j < chldSize; j++)
        {
            Order childOrder = (Order)childOrders.get(j);
            if(childOrder instanceof MTOOrder)
            {
                int childDestination = ((MTOOrder)childOrder).getDestination().getProvince().getId();
                Order otherOrder = location2order[childDestination];
                if(otherOrder != null)
                    if(otherOrder instanceof MTOOrder)
                    {
                        int childLocation = ((MTOOrder)childOrder).getLocation().getProvince().getId();
                        int otherDestination = ((MTOOrder)otherOrder).getDestination().getProvince().getId();
                        if(childLocation == otherDestination)
                            return false;
                    } else
                    {
                        return false;
                    }
            }
        }

        for(int i = 0; i < brSize; i++)
        {
            Order branchOrder = (Order)branchOrders.get(i);
            if(branchOrder instanceof MTOOrder)
            {
                int branchDestination = ((MTOOrder)branchOrder).getDestination().getProvince().getId();
                Order otherOrder = location2order[branchDestination];
                if(otherOrder != null && !(otherOrder instanceof MTOOrder))
                    return false;
            }
        }

        return true;
    }

    static boolean hasNoSelfCuts_(List branchOrders, List childOrders)
    {
        int brSize = branchOrders.size();
        int chldSize = childOrders.size();
        for(int i = 0; i < brSize; i++)
        {
            Order supOrder = (Order)branchOrders.get(i);
            if(!(supOrder instanceof MTOOrder))
            {
                for(int j = 0; j < chldSize; j++)
                {
                    Order mtoOrder = (Order)childOrders.get(j);
                    if((mtoOrder instanceof MTOOrder) && ((MTOOrder)mtoOrder).getDestination().getProvince().getId() == supOrder.getLocation().getProvince().getId())
                        return false;
                }

            }
        }

        for(int j = 0; j < chldSize; j++)
        {
            Order supOrder = (Order)childOrders.get(j);
            if(!(supOrder instanceof MTOOrder))
            {
                for(int i = 0; i < brSize; i++)
                {
                    Order mtoOrder = (Order)branchOrders.get(i);
                    if((mtoOrder instanceof MTOOrder) && ((MTOOrder)mtoOrder).getDestination().getProvince().getId() == supOrder.getLocation().getProvince().getId())
                        return false;
                }

            }
        }

        return true;
    }

    static boolean hasNoSelfCuts_(List orders)
    {
        for(Iterator iterator = orders.iterator(); iterator.hasNext();)
        {
            Order supOrder = (Order)iterator.next();
            if(!(supOrder instanceof MTOOrder))
            {
                for(Iterator iterator1 = orders.iterator(); iterator1.hasNext();)
                {
                    Order mtoOrder = (Order)iterator1.next();
                    if((mtoOrder instanceof MTOOrder) && ((MTOOrder)mtoOrder).getDestination().getProvince().getId() == supOrder.getLocation().getProvince().getId())
                        return false;
                }

            }
        }

        return true;
    }

    static boolean hasNoSelfCuts(List orders)
    {
        int size = orders.size();
        Arrays.fill(location2order, null);
        for(int i = 0; i < size; i++)
            location2order[((Order)orders.get(i)).getLocation().getProvince().getId()] = (Order)orders.get(i);

        for(int i = 0; i < size; i++)
        {
            Order order = (Order)orders.get(i);
            if(order instanceof MTOOrder)
            {
                int dest = ((MTOOrder)order).getDestination().getProvince().getId();
                if(location2order[dest] != null)
                    if(location2order[dest] instanceof MTOOrder)
                    {
                        int loc = ((MTOOrder)order).getLocation().getProvince().getId();
                        int dest2 = ((MTOOrder)location2order[dest]).getDestination().getProvince().getId();
                        if(dest2 == loc)
                            return false;
                    } else
                    {
                        return false;
                    }
            }
        }

        return true;
    }

    static boolean areCompatible(Order order1, Order order2)
    {
        Order supportedOrder1 = getSupportedOrder(order1);
        Order supportedOrder2 = getSupportedOrder(order2);
        return supportedOrder1.getLocation().getId() == supportedOrder2.getLocation().getId();
    }

    static Order getSupportedOrder(Order order)
    {
        if(order instanceof SUPMTOOrder)
            return ((SUPMTOOrder)order).getSupportedOrder();
        if(order instanceof SUPOrder)
            return ((SUPOrder)order).getSupportedOrder();
        else
            throw new IllegalArgumentException("IllegalPlanTable.getSupportedOrder() Error!");
    }

    static Order orderMap[] = new Order[120];
    static Order replacedHLDOrders[] = new Order[120];
    static Order location2order[] = new Order[75];

}
