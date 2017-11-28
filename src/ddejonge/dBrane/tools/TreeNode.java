// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TreeNode.java

package ddejonge.dBrane.tools;

import java.util.*;

public class TreeNode
{

    public TreeNode(Object label)
    {
        numGenerated = 0;
        depth = 0;
        id = numGenerated++;
        parent = null;
        this.label = label;
    }

    public TreeNode(TreeNode parent, Object label)
    {
        numGenerated = 0;
        depth = 0;
        id = numGenerated++;
        if(parent != null)
            parent.addChild(this);
        this.label = label;
    }

    public Object getLabel()
    {
        return label;
    }

    public void addChild(TreeNode childNode)
    {
        if(children == null)
            children = new ArrayList();
        children.add(childNode);
        childNode.parent = this;
        childNode.depth = depth + 1;
    }

    public void addChildren(List childNodes)
    {
        if(children == null)
            children = new ArrayList();
        children.addAll(childNodes);
        for(Iterator iterator = childNodes.iterator(); iterator.hasNext();)
        {
            TreeNode childNode = (TreeNode)iterator.next();
            childNode.parent = this;
            childNode.depth = depth + 1;
        }

    }

    public void removeChild(TreeNode childNode)
    {
        children.remove(childNode);
        childNode.parent = null;
        childNode.depth = 0;
    }

    public TreeNode getParent()
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

    public int getDepth()
    {
        return depth;
    }

    public void getBranchLabels(List listToFill)
    {
        for(TreeNode loopNode = this; loopNode != null; loopNode = loopNode.parent)
            listToFill.add(loopNode.label);

    }

    public String toString()
    {
        return label.toString();
    }

    int numGenerated;
    int id;
    int depth;
    TreeNode parent;
    List children;
    Object label;
}
