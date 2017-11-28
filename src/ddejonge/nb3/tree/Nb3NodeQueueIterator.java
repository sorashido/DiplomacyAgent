// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3NodeQueue.java

package ddejonge.nb3.tree;

import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package ddejonge.nb3.tree:
//            Nb3Node

class Nb3NodeQueueIterator
    implements Iterator
{

    Nb3NodeQueueIterator(ArrayList iteratorList)
    {
        this.iteratorList = iteratorList;
    }

    public boolean hasNext()
    {
        for(int i = 0; i < iteratorList.size(); i++)
            if(((Iterator)iteratorList.get(i)).hasNext())
                return true;

        return false;
    }

    public Nb3Node next()
    {
        for(int i = 0; i < iteratorList.size(); i++)
            if(((Iterator)iteratorList.get(i)).hasNext())
                return (Nb3Node)((Iterator)iteratorList.get(i)).next();

        return null;
    }

    public void remove()
    {
    }

    public volatile Object next()
    {
        return next();
    }

    ArrayList iteratorList;
}
