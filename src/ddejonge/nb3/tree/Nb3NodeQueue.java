// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3NodeQueue.java

package ddejonge.nb3.tree;

import java.util.*;

// Referenced classes of package ddejonge.nb3.tree:
//            Nb3Node, Nb3NodeQueueIterator

public class Nb3NodeQueue
    implements Iterable
{

    public Nb3NodeQueue()
    {
        this(1, 20000);
    }

    public Nb3NodeQueue(int capacity)
    {
        this(1, capacity);
    }

    public Nb3NodeQueue(int numQueues, int capacity)
    {
        this.numQueues = numQueues;
        queues = new ArrayList(numQueues);
        for(int i = 0; i < numQueues; i++)
            queues.add(new PriorityQueue(capacity));

    }

    protected PriorityQueue getQueue(int typeOfNodeToSplit)
    {
        return (PriorityQueue)queues.get(typeOfNodeToSplit);
    }

    public void add(Nb3Node n)
    {
        getQueue(n.getType()).add(n);
    }

    public Nb3Node poll(int typeOfNodeToSplit)
    {
        return (Nb3Node)getQueue(typeOfNodeToSplit).poll();
    }

    public Nb3Node peek(int typeOfNodeToSplit)
    {
        return (Nb3Node)getQueue(typeOfNodeToSplit).peek();
    }

    public String sizes()
    {
        String sizesString = "(";
        for(int i = 0; i < numQueues - 1; i++)
        {
            sizesString = (new StringBuilder(String.valueOf(sizesString))).append(((PriorityQueue)queues.get(i)).size()).toString();
            sizesString = (new StringBuilder(String.valueOf(sizesString))).append(", ").toString();
        }

        sizesString = (new StringBuilder(String.valueOf(sizesString))).append(((PriorityQueue)queues.get(numQueues - 1)).size()).toString();
        sizesString = (new StringBuilder(String.valueOf(sizesString))).append(")").toString();
        return sizesString;
    }

    public int size()
    {
        int size = 0;
        for(int i = 0; i < numQueues; i++)
            size += ((PriorityQueue)queues.get(i)).size();

        return size;
    }

    public void reorder(int adaptedAgentIndex)
    {
        for(int i = 0; i < numQueues; i++)
        {
            int s = ((PriorityQueue)queues.get(i)).size();
            if(s == 0)
                s++;
            PriorityQueue newQueue = new PriorityQueue(s);
            newQueue.addAll((Collection)queues.get(i));
            queues.set(i, newQueue);
        }

    }

    public void clear()
    {
        PriorityQueue q;
        for(Iterator iterator1 = queues.iterator(); iterator1.hasNext(); q.clear())
            q = (PriorityQueue)iterator1.next();

    }

    public Iterator iterator()
    {
        ArrayList iteratorList = new ArrayList(numQueues);
        for(int i = 0; i < numQueues; i++)
            iteratorList.add(((PriorityQueue)queues.get(i)).iterator());

        return new Nb3NodeQueueIterator(iteratorList);
    }

    public static final int defaultCapacity = 20000;
    protected int numQueues;
    protected ArrayList queues;
}
