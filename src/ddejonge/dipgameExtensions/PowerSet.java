// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PowerSet.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.Power;
import java.util.Iterator;

// Referenced classes of package ddejonge.dipgameExtensions:
//            Set, DiplomacyGame

public class PowerSet extends Set
{

    public PowerSet()
    {
        super(7);
    }

    public PowerSet(boolean full)
    {
        super(7, full);
    }

    public PowerSet(int id)
    {
        super(7);
        for(int i = 0; i < 7; i++)
            if((1 << i & id) == 1 << i)
                add(i);

    }

    public PowerSet(PowerSet original)
    {
        super(original);
    }

    public void add(Power pow)
    {
        add(pow.getId());
    }

    public boolean contains(Power pw)
    {
        return contains(pw.getId());
    }

    public void remove(Power pow)
    {
        remove(pow.getId());
    }

    public String toString()
    {
        String s = "{";
        for(Iterator iterator = iterator(); iterator.hasNext();)
        {
            int i = ((Integer)iterator.next()).intValue();
            s = (new StringBuilder(String.valueOf(s))).append(DiplomacyGame.getPower(i).getName()).toString();
            s = (new StringBuilder(String.valueOf(s))).append(" , ").toString();
        }

        if(s.length() > 1)
            s = s.substring(0, s.length() - 3);
        s = (new StringBuilder(String.valueOf(s))).append("}").toString();
        return s;
    }

    public PowerSet complement()
    {
        PowerSet comp = new PowerSet();
        for(int i = 0; i < 7; i++)
            if(!contains(i))
                comp.add(i);

        return comp;
    }

    public boolean equals(Object o)
    {
        if(!(o instanceof PowerSet))
            return false;
        PowerSet ps = (PowerSet)o;
        if(ps.size() != size())
            return false;
        for(Iterator iterator = ps.iterator(); iterator.hasNext();)
        {
            int p = ((Integer)iterator.next()).intValue();
            if(!contains(p))
                return false;
        }

        return true;
    }

    public int getId()
    {
        int id = 0;
        for(int i = 0; i < 7; i++)
            if(contains(i))
                id += 1 << i;

        return id;
    }

    public int hashCode()
    {
        return getId();
    }

    public boolean containsSubset(PowerSet powerSet)
    {
        int x = powerSet.getId();
        return containsSubset(x);
    }

    public boolean containsSubset(int x)
    {
        return (x & getId()) == x;
    }

    public static int getSize(int id)
    {
        return sizes[id];
    }

    public static boolean contains(int powerID, int coalitionID)
    {
        return (1 << powerID & coalitionID) == 1 << powerID;
    }

    static int sizes[];

    static 
    {
        sizes = new int[128];
        for(int coalitionId = 0; coalitionId < 128; coalitionId++)
        {
            int size = 0;
            for(int powerId = 0; powerId < 7; powerId++)
                if(contains(powerId, coalitionId))
                    size++;

            sizes[coalitionId] = size;
        }

    }
}
