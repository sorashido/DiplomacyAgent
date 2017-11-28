// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VariableCluster.java

package ddejonge.dBrane_1_1.andOrSearch;

import ddejonge.dBrane.tools.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class VariableCluster extends TreeNode
{

    VariableCluster()
    {
        super(new ArrayList());
    }

    VariableCluster(List vertices)
    {
        super(vertices);
    }

    VariableCluster(VariableCluster parent, List vertices)
    {
        super(parent, vertices);
    }

    public List getVariables()
    {
        return (List)getLabel();
    }

    public VariableCluster getParent()
    {
        return (VariableCluster)super.getParent();
    }

    public volatile TreeNode getParent()
    {
        return getParent();
    }
}
