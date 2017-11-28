// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AndOrNode.java

package ddejonge.dBrane_1_1.andOrSearch;

import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1.andOrSearch:
//            VariableCluster, Variable

public class AndOrNode
{

    private AndOrNode(AndOrNode parent, AndOrNode localRoot, VariableCluster cluster, int variableIndex, Object label, int type)
    {
        pruned = false;
        reset(parent, localRoot, cluster, variableIndex, label, type);
        numConstructed++;
    }

    private void reset(AndOrNode parent, AndOrNode localRoot, VariableCluster cluster, int variableIndex, Object label, int type)
    {
        id = numGenerated++;
        this.localRoot = localRoot;
        if(parent != null)
            parent.addChild(this);
        this.label = label;
        if(hasChildren())
            throw new IllegalArgumentException("AndOrNode.reset() Error! node is not supposed to have children when it is being reset!");
        this.variableIndex = variableIndex;
        this.type = type;
        if(this.type == 2)
            this.cluster = parent.cluster;
        else
        if(this.type == 1)
            this.cluster = cluster;
        else
        if(this.type == 0)
            this.cluster = null;
        else
            throw new IllegalArgumentException((new StringBuilder("AndOrNode.reset() Error! unknown type: ")).append(type).toString());
        pruned = false;
        open = true;
        ub = 0x7fffffff;
        branchValue = 0;
        nodeValue = 0;
        lb = 0;
    }

    public static AndOrNode getNewRootNode()
    {
        if(recycleBin.size() == 0)
        {
            return new AndOrNode(null, null, null, -1, null, 0);
        } else
        {
            AndOrNode newRoot = (AndOrNode)recycleBin.remove(recycleBin.size() - 1);
            newRoot.reset(null, null, null, -1, null, 0);
            return newRoot;
        }
    }

    public static AndOrNode getNewAndNode(AndOrNode parent, VariableCluster cluster)
    {
        if(recycleBin.size() == 0)
        {
            return new AndOrNode(parent, null, cluster, -1, null, 1);
        } else
        {
            AndOrNode newAndNode = (AndOrNode)recycleBin.remove(recycleBin.size() - 1);
            newAndNode.reset(parent, null, cluster, -1, null, 1);
            return newAndNode;
        }
    }

    public static AndOrNode getNewOrNode(AndOrNode parent, int variableIndex, Object label)
    {
        AndOrNode localRoot;
        if(parent.type == 1)
            localRoot = parent;
        else
            localRoot = parent.localRoot;
        if(recycleBin.size() == 0)
        {
            return new AndOrNode(parent, localRoot, null, variableIndex, label, 2);
        } else
        {
            AndOrNode newOrNode = (AndOrNode)recycleBin.remove(recycleBin.size() - 1);
            newOrNode.reset(parent, localRoot, null, variableIndex, label, 2);
            return newOrNode;
        }
    }

    public static void dispose(AndOrNode node)
    {
        if(node == null)
            return;
        if(!node.isPruned())
            throw new IllegalArgumentException("AndOrNode.dispose() Error! node should first be pruned before being disposed.");
        if(node.hasChildren())
            throw new IllegalArgumentException("AndOrNode.dispose() Error! node is not supposed to have children when disposed.");
        if(node.parent != null)
            throw new IllegalArgumentException("AndOrNode.dispose() Error! node is not supposed to have parent when disposed.");
        numDisposed++;
        if(recycleBin.size() < 10000)
            recycleBin.add(node);
    }

    void prune()
    {
        boolean parentNeedsToBePruned;
        if(type == 0)
            parentNeedsToBePruned = false;
        else
        if(type == 1)
            parentNeedsToBePruned = true;
        else
        if(type == 2)
        {
            parentNeedsToBePruned = true;
            for(Iterator iterator = getParent().getChildren().iterator(); iterator.hasNext();)
            {
                AndOrNode sibling = (AndOrNode)iterator.next();
                if(!sibling.isPruned() && sibling != this)
                {
                    parentNeedsToBePruned = false;
                    break;
                }
            }

        } else
        {
            throw new IllegalArgumentException((new StringBuilder("AndOrNode.prune() Error! Unknown type: ")).append(type).toString());
        }
        if(parentNeedsToBePruned)
            getParent().prune();
        else
            prune(false);
    }

    private void prune(boolean parentIsPrunedToo)
    {
        numPruned++;
        pruned = true;
        if(hasChildren())
        {
            for(Iterator iterator = getChildren().iterator(); iterator.hasNext();)
            {
                AndOrNode child = (AndOrNode)iterator.next();
                if(!child.isPruned())
                    child.prune(true);
            }

            getChildren().clear();
        }
        localRoot = null;
        if(parent != null)
        {
            if(!parentIsPrunedToo)
            {
                parent.getChildren().remove(this);
                parent.updateUB();
            }
            parent = null;
        }
        if(!open)
            dispose(this);
    }

    boolean isPruned()
    {
        return pruned;
    }

    int getNextVarIndex()
    {
        if(type == 1)
            return ((Variable)cluster.getVariables().get(0)).getId();
        if(type == 2)
        {
            for(int i = 0; i < cluster.getVariables().size() - 1; i++)
                if(((Variable)cluster.getVariables().get(i)).getId() == variableIndex)
                    return ((Variable)cluster.getVariables().get(i + 1)).getId();

        }
        return -1;
    }

    void setUB(int ub)
    {
        this.ub = ub;
    }

    void updateUB()
    {
        if(!hasChildren())
            return;
        int oldUB = ub;
        if(((AndOrNode)children.get(0)).type == 1)
            ub = getSumUB();
        else
            ub = getMaxUB();
        if(parent != null && ub != oldUB)
            parent.updateUB();
    }

    private int getMaxUB()
    {
        int max = 0;
        for(Iterator iterator = children.iterator(); iterator.hasNext();)
        {
            AndOrNode child = (AndOrNode)iterator.next();
            if(!child.isPruned() && child.ub > max)
                max = child.ub;
        }

        return max + nodeValue;
    }

    private int getSumUB()
    {
        int sum = 0;
        Iterator iterator = children.iterator();
        while(iterator.hasNext()) 
        {
            AndOrNode child = (AndOrNode)iterator.next();
            if(child.isPruned())
                continue;
            if(child.ub == 0x7fffffff)
            {
                sum = 0x7fffffff;
                break;
            }
            sum += child.ub;
        }
        return sum + nodeValue;
    }

    public int getUB()
    {
        return ub;
    }

    public void updateLB()
    {
        if(!hasChildren())
        {
            lb = ub;
            parent.updateLB(lb);
        } else
        {
            if(type == 1)
                throw new IllegalArgumentException("AndOrNode.updateLB() Error!");
            if(((AndOrNode)children.get(0)).type == 1)
            {
                int oldLB = lb;
                lb = nodeValue;
                for(Iterator iterator = children.iterator(); iterator.hasNext();)
                {
                    AndOrNode child = (AndOrNode)iterator.next();
                    lb += child.lb;
                }

                if(lb > oldLB && parent != null)
                    parent.updateLB(lb);
            } else
            {
                throw new IllegalArgumentException("AndOrNode.updateLB() Error!");
            }
        }
    }

    private void updateLB(int lb)
    {
        if(!hasChildren())
            throw new IllegalArgumentException("AndOrNode.updateLB() Error!");
        if(type == 1)
        {
            if(lb + nodeValue > this.lb)
            {
                this.lb = lb + nodeValue;
                parent.updateLB();
            }
        } else
        {
            if(((AndOrNode)children.get(0)).type == 1)
                throw new IllegalArgumentException("AndOrNode.updateLB() Error!");
            if(lb + nodeValue > this.lb)
            {
                this.lb = lb + nodeValue;
                parent.updateLB(this.lb);
            }
        }
    }

    public Object getLabel()
    {
        return label;
    }

    void setNodeValue(int val)
    {
        if(val < 0)
            throw new IllegalArgumentException("AndOrNode.setValue() Error! current implementation doesn't allow negative values!");
        nodeValue = val;
        if(parent != null)
            branchValue = parent.branchValue + nodeValue;
    }

    public void addChild(AndOrNode childNode)
    {
        if(children == null)
            children = new ArrayList();
        children.add(childNode);
        childNode.parent = this;
    }

    public void addChildren(List childNodes)
    {
        if(children == null)
            children = new ArrayList();
        children.addAll(childNodes);
        for(Iterator iterator = childNodes.iterator(); iterator.hasNext();)
        {
            AndOrNode childNode = (AndOrNode)iterator.next();
            childNode.parent = this;
        }

    }

    public void removeChild(AndOrNode childNode)
    {
        children.remove(childNode);
        childNode.parent = null;
    }

    public AndOrNode getParent()
    {
        return parent;
    }

    public List getChildren()
    {
        return children;
    }

    public boolean hasChildren()
    {
        return children != null && children.size() > 0;
    }

    public void getBranchLabels(Object arrayToFill[])
    {
        for(AndOrNode loopNode = this; loopNode != null; loopNode = loopNode.parent)
            if(loopNode.variableIndex >= 0)
                arrayToFill[loopNode.variableIndex] = loopNode.label;

    }

    public String toString()
    {
        String s;
        if(type == 0)
            s = (new StringBuilder(String.valueOf(id))).append(" ROOT").toString();
        else
        if(type == 1)
            s = (new StringBuilder(String.valueOf(id))).append(" AND").toString();
        else
        if(type == 2)
            s = (new StringBuilder(String.valueOf(id))).append(" label: ").append(getLabel()).toString();
        else
            throw new IllegalArgumentException("AndOrNode.toString() Error!");
        return (new StringBuilder(String.valueOf(s))).append(" lb: ").append(lb).append(" brVal: ").append(branchValue).append(" ub: ").append(ub).toString();
    }

    static final int ROOT = 0;
    static final int AND = 1;
    static final int OR = 2;
    public static int numGenerated = 0;
    public static int numConstructed = 0;
    public static int numDisposed = 0;
    public static int numPruned = 0;
    public static int numDiscardedDirectly = 0;
    static final int CAPACITY = 10000;
    static ArrayList recycleBin = new ArrayList(10000);
    int id;
    AndOrNode localRoot;
    AndOrNode parent;
    List children;
    Object label;
    int type;
    int variableIndex;
    VariableCluster cluster;
    private boolean pruned;
    boolean open;
    int ub;
    int branchValue;
    int nodeValue;
    int lb;

}
