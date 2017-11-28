// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AndOrTree.java

package ddejonge.dBrane_1_1.andOrSearch;

import ddejonge.dBrane.tools.ArrayOfLists;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1.andOrSearch:
//            ClusterTreeGenerator, ValueCalculator, AndOrNode, VariableCluster, 
//            Graph

public class AndOrTree
{

    public AndOrTree(ArrayOfLists domains, Graph dependencyGraph, ValueCalculator calculator)
    {
        andList = new ArrayList();
        orList = new ArrayList();
        this.domains = domains;
        this.dependencyGraph = dependencyGraph;
        this.calculator = calculator;
    }

    public Object[] expand(long endTime)
    {
        List clusterTreeRoots = ClusterTreeGenerator.createClusterTrees(dependencyGraph, domains);
        calculator.setClusterTrees(clusterTreeRoots);
        return expand(clusterTreeRoots, endTime);
    }

    private Object[] expand(List rootClusters, long endTime)
    {
        _branchLabels = new Object[domains.getArraySize()];
        Object branchLabels[] = _branchLabels;
        rootNode = AndOrNode.getNewRootNode();
        andList.clear();
        orList.clear();
        VariableCluster rootCluster;
        for(Iterator iterator = rootClusters.iterator(); iterator.hasNext(); andList.add(AndOrNode.getNewAndNode(rootNode, rootCluster)))
            rootCluster = (VariableCluster)iterator.next();

        boolean pickAndNode = true;
        while(rootNode.ub > rootNode.lb && (andList.size() > 0 || orList.size() > 0)) 
        {
            long currentTime = System.currentTimeMillis();
            if(currentTime >= endTime)
                break;
            AndOrNode currentNode;
            if(orList.size() == 0)
                currentNode = (AndOrNode)andList.remove(andList.size() - 1);
            else
            if(andList.size() == 0)
                currentNode = (AndOrNode)orList.remove(orList.size() - 1);
            else
            if(pickAndNode)
                currentNode = (AndOrNode)andList.remove(andList.size() - 1);
            else
                currentNode = (AndOrNode)orList.remove(orList.size() - 1);
            currentNode.open = false;
            if(currentNode.isPruned())
                AndOrNode.dispose(currentNode);
            else
            if(currentNode.localRoot != null && currentNode.parent.branchValue + currentNode.getUB() <= currentNode.localRoot.lb && currentNode.localRoot.lb > 0)
            {
                currentNode.prune();
            } else
            {
                int nextVarIndex = currentNode.getNextVarIndex();
                if(nextVarIndex > -1)
                {
                    AndOrNode localRoot;
                    if(currentNode.type == 1)
                        localRoot = currentNode;
                    else
                        localRoot = currentNode.localRoot;
                    List childLabels = domains.get(nextVarIndex);
                    Arrays.fill(branchLabels, null);
                    currentNode.getBranchLabels(branchLabels);
                    calculator.setBranchLabels(branchLabels);
                    int size = childLabels.size();
                    for(int i = 0; i < size; i++)
                    {
                        Object label = childLabels.get(i);
                        calculator.setNewChildLabel(nextVarIndex, label);
                        boolean consistent = calculator.isConsistent();
                        if(consistent)
                        {
                            int nodeValue = calculator.getNodeValue();
                            int ub = calculator.getUB();
                            if(ub + currentNode.branchValue > localRoot.lb || localRoot.lb == 0)
                            {
                                AndOrNode newNode = AndOrNode.getNewOrNode(currentNode, nextVarIndex, label);
                                newNode.setUB(ub);
                                newNode.setNodeValue(nodeValue);
                                orList.add(newNode);
                            }
                        }
                    }

                    if(!currentNode.hasChildren())
                        currentNode.prune();
                    else
                        currentNode.updateUB();
                    pickAndNode = false;
                } else
                if(currentNode.cluster.hasChildren())
                {
                    AndOrNode childNode;
                    for(Iterator iterator3 = currentNode.cluster.getChildren().iterator(); iterator3.hasNext(); andList.add(childNode))
                    {
                        Object chldCluster = iterator3.next();
                        VariableCluster childCluster = (VariableCluster)chldCluster;
                        childNode = AndOrNode.getNewAndNode(currentNode, childCluster);
                        int ub = calculator.calculateUB(childCluster);
                        childNode.setUB(ub);
                    }

                    currentNode.updateUB();
                    pickAndNode = true;
                } else
                {
                    currentNode.updateLB();
                    pickAndNode = true;
                }
            }
        }
        if(rootNode.isPruned())
            return null;
        Object solution[] = new Object[domains.getArraySize()];
        retrieveSolution(rootNode, solution);
        rootNode.prune();
        AndOrNode node;
        for(Iterator iterator1 = andList.iterator(); iterator1.hasNext(); AndOrNode.dispose(node))
        {
            node = (AndOrNode)iterator1.next();
            if(!node.isPruned())
                throw new IllegalArgumentException("AndOrTreeSearch.getSolution() Error!");
        }

        AndOrNode node;
        for(Iterator iterator2 = orList.iterator(); iterator2.hasNext(); AndOrNode.dispose(node))
        {
            node = (AndOrNode)iterator2.next();
            if(!node.isPruned())
                throw new IllegalArgumentException("AndOrTreeSearch.getSolution() Error!");
        }

        return solution;
    }

    public void getNewSolution(Object solution[])
    {
        retrieveSolution(rootNode, solution);
    }

    private void retrieveSolution(AndOrNode node, Object solution[])
    {
        if(!node.hasChildren())
            return;
        float maxVal = -1F;
        List bestChildren = new ArrayList(4);
        for(Iterator iterator = node.getChildren().iterator(); iterator.hasNext();)
        {
            AndOrNode child = (AndOrNode)iterator.next();
            if(!child.isPruned())
                if(child.type == 2)
                {
                    if((float)child.lb > maxVal)
                    {
                        maxVal = child.lb;
                        bestChildren.clear();
                        bestChildren.add(child);
                    } else
                    if((float)child.lb == maxVal)
                        bestChildren.add(child);
                } else
                {
                    retrieveSolution(child, solution);
                }
        }

        if(bestChildren != null && bestChildren.size() > 0)
        {
            int r = random.nextInt(bestChildren.size());
            AndOrNode bestChild = (AndOrNode)bestChildren.get(r);
            solution[bestChild.variableIndex] = bestChild.getLabel();
            retrieveSolution(bestChild, solution);
        }
    }

    void retrieveFirstBestSolution(AndOrNode node, Object solution[])
    {
        if(!node.hasChildren())
            return;
        float maxVal = -1F;
        AndOrNode bestChild = null;
        for(Iterator iterator = node.getChildren().iterator(); iterator.hasNext();)
        {
            AndOrNode child = (AndOrNode)iterator.next();
            if(!child.isPruned())
                if(child.type == 2)
                {
                    if(bestChild == null || (float)child.getUB() > maxVal)
                    {
                        maxVal = child.getUB();
                        bestChild = child;
                    }
                } else
                {
                    retrieveFirstBestSolution(child, solution);
                }
        }

        if(bestChild != null)
        {
            solution[bestChild.variableIndex] = bestChild.getLabel();
            retrieveFirstBestSolution(bestChild, solution);
        }
    }

    static Random random = new Random();
    private List andList;
    private List orList;
    private Object _branchLabels[];
    ArrayOfLists domains;
    Graph dependencyGraph;
    ValueCalculator calculator;
    AndOrNode rootNode;

}
