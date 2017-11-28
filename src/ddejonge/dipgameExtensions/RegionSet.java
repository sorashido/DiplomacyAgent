// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RegionSet.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.Region;
import java.util.*;

public class RegionSet
    implements Iterable
{

    public RegionSet()
    {
        regionsArray = new Region[120];
        regionsList = new ArrayList(120);
    }

    public void add(Region r)
    {
        if(!contains(r))
        {
            regionsArray[r.getId()] = r;
            regionsList.add(r);
        }
    }

    public void remove(Region r)
    {
        regionsArray[r.getId()] = null;
        regionsList.remove(r);
    }

    public void addAll(RegionSet regions)
    {
        Region r;
        for(Iterator iterator1 = regions.iterator(); iterator1.hasNext(); add(r))
            r = (Region)iterator1.next();

    }

    public boolean contains(Region r)
    {
        return regionsArray[r.getId()] != null;
    }

    public Iterator iterator()
    {
        return regionsList.iterator();
    }

    public Region get(int index)
    {
        return (Region)regionsList.get(index);
    }

    public void clear()
    {
        Arrays.fill(regionsArray, null);
        regionsList.clear();
    }

    public int size()
    {
        return regionsList.size();
    }

    public boolean isSubsetOf(RegionSet regionSet)
    {
        for(int i = 0; i < regionsArray.length; i++)
            if(regionsArray[i] != null && regionSet.regionsArray[i] == null)
                return false;

        return true;
    }

    public String toString()
    {
        return regionsList.toString();
    }

    private Region regionsArray[];
    private ArrayList regionsList;
}
