// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Node.java

package ddejonge.nb3.tree;

import ddejonge.nb3.algorithm.Nb3AgentSet;
import ddejonge.nb3.algorithm.Nb3Algorithm;
import ddejonge.nb3.domain.Nb3InstanceInfo;
import ddejonge.nb3.tools.ITreeNode;
import java.io.PrintStream;
import java.util.*;

// Referenced classes of package ddejonge.nb3.tree:
//            Nb3Tree, Nb3Label

public class Nb3Node
    implements Comparable, ITreeNode
{

    public Nb3Node(Nb3Tree theTree, Nb3Label rootLabel)
    {
        parent = null;
        children = new ArrayList();
        isOpened = true;
        participatingAgents = null;
        prob = new float[Nb3InstanceInfo.numAgents];
        opponentUtility = -1F;
        expansionHeuristic2 = 0.0F;
        this.theTree = theTree;
        label = rootLabel;
        ub = new float[Nb3InstanceInfo.numAgents];
        e = new float[Nb3InstanceInfo.numAgents];
        lb = new float[Nb3InstanceInfo.numAgents];
        nodeId = theTree.theAlgorithm.numNodes;
        theTree.theAlgorithm.numNodes++;
    }

    public Nb3Node()
    {
        parent = null;
        children = new ArrayList();
        isOpened = true;
        participatingAgents = null;
        prob = new float[Nb3InstanceInfo.numAgents];
        opponentUtility = -1F;
        expansionHeuristic2 = 0.0F;
    }

    public Nb3Node(Nb3Label label)
    {
        parent = null;
        children = new ArrayList();
        isOpened = true;
        participatingAgents = null;
        prob = new float[Nb3InstanceInfo.numAgents];
        opponentUtility = -1F;
        expansionHeuristic2 = 0.0F;
        this.label = label;
    }

    void addChild(Nb3Node newChild)
    {
        children.add(newChild);
    }

    void removeChild(Nb3Node child)
    {
        children.remove(child);
    }

    void setParent(Nb3Node newParent)
    {
        parent = newParent;
    }

    public Nb3Node getParent()
    {
        return parent;
    }

    public float getMyNormalizedUtility()
    {
        return getNormalizedUtility(theTree.theAlgorithm.myAgentNumber);
    }

    public float getNormalizedUtility(int ag)
    {
        if(normalizedUtility == null)
            calculateNormalizedUtilities();
        return normalizedUtility[ag];
    }

    public float getOpponentUtility()
    {
        if(opponentUtility != -1F)
            return opponentUtility;
        opponentUtility = 1.0F;
        if(participatingAgents == null)
            participatingAgents = theTree.theAlgorithm.getParticipatingAgents(getPath());
        for(Iterator iterator = participatingAgents.iterator(); iterator.hasNext();)
        {
            int ag = ((Integer)iterator.next()).intValue();
            float x;
            if(ag == theTree.theAlgorithm.myAgentNumber)
                x = 1.0F;
            else
                x = getNormalizedUtility(ag);
            if(x < 0.0F)
            {
                opponentUtility = 0.0F;
                return 0.0F;
            }
            opponentUtility *= x;
        }

        return opponentUtility;
    }

    private void calculateNormalizedUtilities()
    {
        float utility = -1F;
        float range = -1F;
        normalizedUtility = new float[Nb3InstanceInfo.numAgents];
        for(int n = 0; n < Nb3InstanceInfo.numAgents; n++)
        {
            utility = e[n] - Nb3Tree.original_rv[n];
            range = Nb3Tree.original_gub[n] - Nb3Tree.original_rv[n];
            if(utility >= 0.0F && (double)range < 0.001D)
                normalizedUtility[n] = 0.0F;
            else
                normalizedUtility[n] = utility / range;
        }

    }

    public int getType()
    {
        return label.getType();
    }

    public float utility(int n)
    {
        return e[n] - theTree.rv[n];
    }

    public float potential(int n)
    {
        return ub[n] - lb[n];
    }

    public List getChildren()
    {
        return children;
    }

    public ArrayList getPath()
    {
        ArrayList path = new ArrayList();
        addLabelsToPath(path);
        return path;
    }

    void addLabelsToPath(ArrayList path)
    {
        if(label == null)
            System.out.println((new StringBuilder("xNode.addLabelsToPath() ")).append(nodeId).toString());
        else
            path.add(0, label);
        if(parent == null)
        {
            return;
        } else
        {
            parent.addLabelsToPath(path);
            return;
        }
    }

    public float getExpansionHeuristic()
    {
        return expansionHeuristic;
    }

    public float getExpansionHeuristic2()
    {
        return expansionHeuristic2;
    }

    public void setExpansionHeuristic(float eh)
    {
        expansionHeuristic = eh;
    }

    public void setExpansionHeuristic(float eh1, float eh2)
    {
        expansionHeuristic = eh1;
        expansionHeuristic2 = eh2;
    }

    public int compareTo(Nb3Node x)
    {
        if(getExpansionHeuristic() > x.getExpansionHeuristic())
            return -1;
        if(getExpansionHeuristic() < x.getExpansionHeuristic())
            return 1;
        if(getExpansionHeuristic2() > x.getExpansionHeuristic2())
            return -1;
        return getExpansionHeuristic2() >= x.getExpansionHeuristic2() ? 0 : 1;
    }

    public String toString()
    {
        String s = label.toString();
        return (new StringBuilder(String.valueOf(s))).append(" ").append("exp: ").append(Math.round(getExpansionHeuristic() * 100F)).toString();
    }

    public volatile ITreeNode getParent()
    {
        return getParent();
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((Nb3Node)obj);
    }

    public Nb3Node parent;
    public Nb3Tree theTree;
    public int nodeId;
    ArrayList children;
    public boolean isOpened;
    Nb3AgentSet participatingAgents;
    public Nb3Label label;
    public float ub[];
    public float e[];
    public float lb[];
    private float normalizedUtility[];
    public float prob[];
    private float opponentUtility;
    private float expansionHeuristic;
    private float expansionHeuristic2;
}
