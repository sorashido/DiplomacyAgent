// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ValueCalculator.java

package ddejonge.dBrane_1_1.andOrSearch;

import java.util.List;

// Referenced classes of package ddejonge.dBrane_1_1.andOrSearch:
//            VariableCluster

public interface ValueCalculator
{

    public abstract void setClusterTrees(List list);

    public abstract void setBranchLabels(Object aobj[]);

    public abstract void setNewChildLabel(int i, Object obj);

    public abstract boolean isConsistent();

    public abstract int getUB();

    public abstract int getNodeValue();

    public abstract int calculateUB(VariableCluster variablecluster);
}
