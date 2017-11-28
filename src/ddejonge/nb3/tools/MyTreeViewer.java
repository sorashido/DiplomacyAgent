// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MyTreeViewer.java

package ddejonge.nb3.tools;

import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

// Referenced classes of package ddejonge.nb3.tools:
//            ITreeNode

public class MyTreeViewer extends JFrame
{

    public static void main(String args1[])
    {
    }

    public MyTreeViewer(ITreeNode root, String title)
    {
        setTitle(title);
        int x = (int)Math.round(100D * Math.random());
        int y = (int)Math.round(100D * Math.random());
        setBounds(x, y, 500, 500);
        DefaultMutableTreeNode dmt_root = new DefaultMutableTreeNode(root);
        addChildren(dmt_root);
        JTree tree = new JTree(dmt_root);
        add(new JScrollPane(tree));
        setVisible(true);
    }

    void addChildren(DefaultMutableTreeNode dmt_node)
    {
        List objectChildren = ((ITreeNode)dmt_node.getUserObject()).getChildren();
        if(objectChildren == null || objectChildren.size() == 0)
            return;
        DefaultMutableTreeNode newChild;
        for(Iterator iterator = objectChildren.iterator(); iterator.hasNext(); addChildren(newChild))
        {
            ITreeNode child = (ITreeNode)iterator.next();
            newChild = new DefaultMutableTreeNode(child);
            dmt_node.add(newChild);
        }

    }
}
