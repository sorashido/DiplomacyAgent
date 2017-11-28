// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderParser.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.orders.*;
import java.util.Iterator;
import java.util.Vector;

// Referenced classes of package ddejonge.dipgameExtensions:
//            DiplomacyGame

public class OrderParser
{

    public OrderParser()
    {
    }

    public static Order string2Order(String orderString)
    {
        orderString = orderString.trim();
        if(orderString.contains(" SUP "))
            if(orderString.contains("MTO"))
                return string2SupportMoveOrder(orderString);
            else
                return string2SupportOrder(orderString);
        if(orderString.contains(" HLD"))
            return string2HLDOrder(orderString);
        if(orderString.contains(" MTO "))
            return string2MTOOrder(orderString);
        else
            throw new IllegalArgumentException((new StringBuilder("Converter.string2Order() Error! ")).append(orderString).toString());
    }

    public static HLDOrder string2HLDOrder(String orderString)
    {
        orderString = orderString.replace(" HLD", "");
        String unitString[] = orderString.split(" ");
        Power power = DiplomacyGame.getPower(unitString[1]);
        Region region = getLocation(unitString);
        return new HLDOrder(power, region);
    }

    public static MTOOrder string2MTOOrder(String orderString)
    {
        String s[] = orderString.split(" MTO ");
        String unitStrings[] = s[0].split(" ");
        String destinationStrings[] = s[1].split(" ");
        Power power = DiplomacyGame.getPower(unitStrings[1]);
        Region location = getLocation(unitStrings);
        Region destination = null;
        if(destinationStrings.length == 1)
        {
            String destinationName = (new StringBuilder(String.valueOf(destinationStrings[0]))).append(unitStrings[2]).toString();
            String provinceName = destinationStrings[0];
            destination = getRegionByName(DiplomacyGame.getProvince(provinceName), destinationName);
        } else
        {
            String destinationName = (new StringBuilder(String.valueOf(destinationStrings[1]))).append(destinationStrings[2]).toString();
            String provinceName = destinationStrings[1];
            destination = getRegionByName(DiplomacyGame.getProvince(provinceName), destinationName);
        }
        return new MTOOrder(power, location, destination);
    }

    public static Order string2SupportMoveOrder(String orderString)
    {
        String s[] = orderString.split(" SUP ");
        String locationStrings[] = s[0].split(" ");
        Power power = DiplomacyGame.getPower(locationStrings[1]);
        Region location = getLocation(locationStrings);
        MTOOrder moveOrder = string2MTOOrder(s[1]);
        return new SUPMTOOrder(power, location, moveOrder);
    }

    public static Order string2SupportOrder(String orderString)
    {
        String s[] = orderString.split(" SUP ");
        String locationStrings[] = s[0].split(" ");
        Power power = DiplomacyGame.getPower(locationStrings[1]);
        Region location = getLocation(locationStrings);
        HLDOrder supportedOrder = string2HLDOrder(s[1]);
        return new SUPOrder(power, location, supportedOrder);
    }

    public static Region getLocation(String unitStrings[])
    {
        Province prov = null;
        String locationName;
        if(unitStrings.length == 5)
        {
            prov = DiplomacyGame.getProvince(unitStrings[3]);
            locationName = (new StringBuilder(String.valueOf(unitStrings[3]))).append(unitStrings[2]).toString();
        } else
        {
            prov = DiplomacyGame.getProvince(unitStrings[4]);
            locationName = (new StringBuilder(String.valueOf(unitStrings[4]))).append(unitStrings[5]).toString();
        }
        return getRegionByName(prov, locationName);
    }

    public static Region getRegionByName(Province prov, String regionName)
    {
        for(Iterator iterator = prov.getRegions().iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            if(r.getName().equalsIgnoreCase(regionName))
                return r;
        }

        return null;
    }
}
