// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Graph.java

package ddejonge.dBrane_1_1.andOrSearch;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package ddejonge.dBrane_1_1.andOrSearch:
//            Variable

public class Graph
{

    public Graph(int numVariables)
    {
        variables = new ArrayList(numVariables);
        for(int i = 0; i < numVariables; i++)
            variables.add(new Variable(i));

    }

    Graph(List variables)
    {
        this.variables = variables;
    }

    public List getVariables()
    {
        return variables;
    }

    public void setEdge(int v0, int v1)
    {
        Variable var0 = null;
        Variable var1 = null;
        for(int i = 0; i < variables.size(); i++)
        {
            if(((Variable)variables.get(i)).getId() == v0)
                var0 = (Variable)variables.get(i);
            if(((Variable)variables.get(i)).getId() == v1)
                var1 = (Variable)variables.get(i);
        }

        setEdge(var0, var1);
    }

    public void setEdge(Variable v0, Variable v1)
    {
        v0.getNeighbors().add(v1);
        v1.getNeighbors().add(v0);
    }

    List variables;
}
