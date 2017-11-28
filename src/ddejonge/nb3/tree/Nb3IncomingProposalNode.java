// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3IncomingProposalNode.java

package ddejonge.nb3.tree;

import ddejonge.nb3.domain.Nb3Message;
import java.io.PrintStream;
import java.util.ArrayList;

// Referenced classes of package ddejonge.nb3.tree:
//            Nb3Node, Nb3Tree, Nb3Label

public class Nb3IncomingProposalNode extends Nb3Node
{

    public Nb3IncomingProposalNode(Nb3Tree theTree, Nb3Message msg, ArrayList path, Nb3Label label, float lb[], float e[], float ub[])
    {
        proposalID = msg.getConversationId();
        proposer = msg.getSender();
        this.path = new ArrayList(path);
        this.ub = ub;
        this.e = e;
        this.lb = lb;
        this.theTree = theTree;
        this.label = label;
    }

    public int getProposer()
    {
        return proposer;
    }

    void addLabelsToPath(ArrayList path)
    {
        if(label == null)
            System.out.println((new StringBuilder("xNode.addLabelsToPath() ")).append(nodeId).toString());
        else
            path.add(0, label);
        path.addAll(0, this.path);
    }

    protected ArrayList path;
    int proposer;
    public String proposalID;
}
