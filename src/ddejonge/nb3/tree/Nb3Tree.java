// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Tree.java

package ddejonge.nb3.tree;

import ddejonge.nb3.algorithm.Nb3Algorithm;
import ddejonge.nb3.domain.Nb3Heuristics;
import ddejonge.nb3.domain.Nb3InstanceInfo;
import java.util.*;

// Referenced classes of package ddejonge.nb3.tree:
//            Nb3Node, Nb3NodeQueue, Nb3IncomingProposalNode, Nb3Label

public class Nb3Tree
{

    public Nb3Tree(Nb3Algorithm owner, Nb3NodeQueue queue, Nb3Label rootLabel)
    {
        openList = new LinkedList();
        closedList = new LinkedList();
        theAlgorithm = owner;
        this.queue = queue;
        root = new Nb3Node(this, rootLabel);
        selectedNode = root;
        openList.add(root);
        queue.add(root);
        gub = new float[Nb3InstanceInfo.numAgents];
        rv = new float[Nb3InstanceInfo.numAgents];
        glb = new float[Nb3InstanceInfo.numAgents];
        offer = new float[Nb3InstanceInfo.numAgents];
    }

    public void addNode(Nb3Node newChild)
    {
        newChild.theTree = this;
        openList.add(newChild);
        newChild.nodeId = theAlgorithm.numNodes;
        theAlgorithm.numNodes++;
        if(!(newChild instanceof Nb3IncomingProposalNode))
        {
            newChild.setParent(selectedNode);
            selectedNode.addChild(newChild);
        }
    }

    public boolean SelectNode()
    {
        if(openList.size() == 0)
        {
            return false;
        } else
        {
            selectedNode = (Nb3Node)openList.get(0);
            return true;
        }
    }

    public void selectNode(Nb3Node newSelection)
    {
        selectedNode = newSelection;
    }

    public Nb3Node getSelectedNode()
    {
        return selectedNode;
    }

    public void closeNode(Nb3Node node)
    {
        openList.remove(node);
        closedList.add(node);
        node.isOpened = false;
    }

    public void resetRoot(Nb3Node newRoot)
    {
        Nb3Node node = newRoot;
        Nb3Node newParent = null;
        Nb3Node oldParent;
        for(; node != null; node = oldParent)
        {
            oldParent = node.getParent();
            node.setParent(newParent);
            if(newParent != null)
                node.removeChild(newParent);
            if(oldParent != null)
                node.addChild(oldParent);
            newParent = node;
        }

    }

    public void reorderQueue(int adaptedAgentIndex)
    {
        Nb3Node n;
        for(Iterator iterator = queue.iterator(); iterator.hasNext(); theAlgorithm.theHeuristics.reCalculateExpansionHeuristic(n, adaptedAgentIndex))
            n = (Nb3Node)iterator.next();

        queue.reorder(adaptedAgentIndex);
    }

    public double getMyCurrentNormalizedUtility()
    {
        return getCurrentNormalizedUtility(theAlgorithm.myAgentNumber);
    }

    public double getCurrentNormalizedUtility(int ag)
    {
        return (double)root.getNormalizedUtility(ag);
    }

    public void clear()
    {
        openList.clear();
        closedList.clear();
        queue.clear();
    }

    public Nb3Node root;
    private Nb3Node selectedNode;
    public List openList;
    List closedList;
    public Nb3NodeQueue queue;
    public static float original_gub[] = null;
    public static float original_rv[] = null;
    public float gub[];
    public float rv[];
    public float glb[];
    public float offer[];
    public Nb3Algorithm theAlgorithm;

}
