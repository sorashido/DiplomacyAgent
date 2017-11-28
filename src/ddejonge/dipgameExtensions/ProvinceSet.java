// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProvinceSet.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.Province;

// Referenced classes of package ddejonge.dipgameExtensions:
//            Set

public class ProvinceSet extends Set
{

    public ProvinceSet()
    {
        super(75);
    }

    public void add(Province p)
    {
        add(p.getId());
    }

    public boolean contains(Province p)
    {
        return contains(p.getId());
    }
}
