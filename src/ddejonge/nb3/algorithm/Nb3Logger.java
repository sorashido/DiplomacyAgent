// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Logger.java

package ddejonge.nb3.algorithm;

import ddejonge.nb3.domain.Nb3InstanceInfo;
import ddejonge.nb3.domain.Nb3Message;
import ddejonge.nb3.tools.DaveLogger;
import ddejonge.nb3.tree.*;
import java.util.Iterator;

// Referenced classes of package ddejonge.nb3.algorithm:
//            Nb3Utilities, Nb3Proposal, Nb3AgentSet

public class Nb3Logger extends DaveLogger
{

    public Nb3Logger()
    {
        enabled = false;
    }

    public Nb3Logger(int _capacity)
    {
        super(_capacity);
        enabled = false;
    }

    public void enable(String _agentName, String _runFolderPath, String _fileName)
    {
        super.enable(_runFolderPath, _fileName);
    }

    public void logNode(Nb3Node node)
    {
        logSeparationLineHead();
        logln((new StringBuilder(String.valueOf(node.nodeId))).append(". New node added to the tree of type: ").append(node.label.getType()).toString());
        if(node.parent != null)
            logln((new StringBuilder("parent: ")).append(node.parent.nodeId).toString());
        logln((new StringBuilder("label: ")).append(node.label.toString()).toString());
        logln("********");
    }

    public void logOrigBounds(Nb3Tree theTree)
    {
        logln("");
        logln("\t");
        for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            log((new StringBuilder(String.valueOf(Nb3InstanceInfo.agentNames[i]))).append("     ").toString());

        if(Nb3Tree.original_gub != null)
        {
            logln("or. gub");
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
                log((new StringBuilder("\t")).append(Nb3Tree.original_gub[i]).toString());

        }
        if(Nb3Tree.original_rv != null)
        {
            logln("or. rv");
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
                log((new StringBuilder("\t")).append(Nb3Tree.original_rv[i]).toString());

        }
    }

    public void logBounds(Nb3Node node)
    {
        int columnWidth = 5;
        logln("");
        String s;
        for(s = ""; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
        logln(s);
        for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
        {
            for(s = Nb3InstanceInfo.agentNames[i]; s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
            log(s);
        }

        if(node.theTree.gub != null)
        {
            for(s = "gub"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                float r = (float)Math.round(node.theTree.gub[i] * 10F) / 10F;
                for(s = (new StringBuilder()).append(r).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.ub != null)
        {
            for(s = "ub"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                float r = (float)Math.round(node.ub[i] * 10F) / 10F;
                for(s = (new StringBuilder()).append(r).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.theTree.rv != null)
        {
            for(s = "rv"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                float r = (float)Math.round(node.theTree.rv[i] * 10F) / 10F;
                for(s = (new StringBuilder()).append(r).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.e != null)
        {
            for(s = "e"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                float r = (float)Math.round(node.e[i] * 10F) / 10F;
                for(s = (new StringBuilder()).append(r).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.lb != null)
        {
            for(s = "lb"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                float r = (float)Math.round(node.lb[i] * 10F) / 10F;
                for(s = (new StringBuilder()).append(r).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.theTree.glb != null)
        {
            for(s = "glb"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                float r = (float)Math.round(node.theTree.glb[i] * 10F) / 10F;
                for(s = (new StringBuilder()).append(r).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.e != null)
        {
            logln("");
            for(s = "nut"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                for(s = (new StringBuilder()).append((int)(100F * node.getNormalizedUtility(i))).toString(); s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        if(node.prob != null)
        {
            for(s = "prb"; s.length() < columnWidth; s = (new StringBuilder(String.valueOf(s))).append(" ").toString());
            logln(s);
            for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
            {
                if(node.prob[i] < 0.0F)
                    s = "-";
                else
                    s = (new StringBuilder()).append((int)(100F * node.prob[i])).toString();
                for(; s.length() < columnWidth; s = (new StringBuilder(" ")).append(s).toString());
                log(s);
            }

        }
        logln((new StringBuilder("Expansion heuristic: ")).append(node.getExpansionHeuristic()).toString());
        logSeparationLineFoot();
    }

    public void logBestProposals(Nb3Proposal bestFoundByUs, Nb3Proposal bestProposedToUs, double my_asp, double opp_asp)
    {
        logln("");
        logln("");
        logln("---------------------------------------");
        logln("\tmin\t\tf.ByMe\t\tp.2Us");
        String newLine = (new StringBuilder("myAsp\t")).append(Nb3Utilities.double2Percentage(my_asp)).toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append("\t\t").toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append(sat2percentage(bestFoundByUs, true)).toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append("\t\t").toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append(sat2percentage(bestProposedToUs, true)).toString();
        logln(newLine);
        newLine = (new StringBuilder("oppAsp\t")).append(Nb3Utilities.double2Percentage(opp_asp)).toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append("\t\t").toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append(sat2percentage(bestFoundByUs, false)).toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append("\t\t").toString();
        newLine = (new StringBuilder(String.valueOf(newLine))).append(sat2percentage(bestProposedToUs, false)).toString();
        logln(newLine);
    }

    private String sat2percentage(Nb3Proposal prop, boolean displayMySatisfaction)
    {
        if(prop == null)
            return "-";
        int percentage;
        if(displayMySatisfaction)
            percentage = Nb3Utilities.double2Percentage(prop.myNormalizedUtility);
        else
            percentage = Nb3Utilities.double2Percentage(prop.oppNormalizedUtility);
        return Integer.toString(percentage);
    }

    public void logProposalMade(Nb3Proposal proposal, int myAgentID, float my_original_gub, double my_asp, double opp_asp)
    {
        logln();
        logln("*****Nb3Logger.logProposalMade() WE ARE PROPOSING:");
        logln(proposal.toString());
        logln((new StringBuilder("proposal id: ")).append(proposal.getID()).toString());
        logln((new StringBuilder("node id: ")).append(proposal.correspondingNodeId).toString());
        int ag;
        for(Iterator iterator = proposal.getParticipatingAgents().iterator(); iterator.hasNext(); logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.agentNames[ag]))).append(" normalized utility: ").append(proposal.normalizedUtility[ag]).toString()))
            ag = ((Integer)iterator.next()).intValue();

        logln((new StringBuilder("my asp level: ")).append(my_asp).toString());
        logln((new StringBuilder("opp asp level: ")).append(opp_asp).toString());
        logln((new StringBuilder("my orig gub: ")).append(my_original_gub).toString());
        logln((new StringBuilder("my e: ")).append(proposal.e[myAgentID]).toString());
        logln("*****");
        logln();
        logln("---------------------------------------");
    }

    public void logIllegalProposalReceived(String myAgentName, Nb3Message msg)
    {
        logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" ").append(myAgentName).append(": illegal proposal received ").append(msg.getConversationId()).toString());
    }

    public void logProposalReceived(String myAgentName, Nb3Message msg)
    {
        logln();
        logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" ").append(myAgentName).append(": Proposal received ").append(msg.getConversationId()).toString());
    }

    public void logIncomingProposalNode(Nb3Node ipn)
    {
        logBounds(ipn);
    }

    public void logNoNodeToSplit()
    {
    }

    public void logNoLabelToSplit(Nb3Node nb3node)
    {
    }

    public void logEmptyQueue(String codeLocation, int typeOfNodeToSplit, Nb3NodeQueue que)
    {
        logln((new StringBuilder(String.valueOf(codeLocation))).append(": theTree.queue is empty. type of node to split: ").append(typeOfNodeToSplit).append(" sizes of queues: ").append(que.sizes()).toString());
    }

    public void logChosenNode(String codeLocation, Nb3Node maxNode)
    {
        logln((new StringBuilder(String.valueOf(codeLocation))).append(": node chosen to expand: ").append(maxNode.nodeId).append(" with exp heur ").append(maxNode.getExpansionHeuristic()).toString());
    }

    public void logIncomingAcceptance(Nb3Proposal acceptedProp, int accepter)
    {
        logln("");
        logln("");
        logln("---------------------------------------");
        logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" ").append(acceptedProp.getID()).append(" ACCEPTED by ").append(accepter).toString());
        logln("---------------------------------------");
        logln("");
        logln("");
    }

    public void logSeparationLineHead()
    {
        logln("");
        logln("");
        logln("---------------------------------------");
    }

    public void logSeparationLineFoot()
    {
        logln("---------------------------------------");
        logln("");
        logln("");
    }

    public void logAcceptedProposal(Nb3Proposal nb3proposal)
    {
    }

    public void logDealConfirmation(Nb3Proposal confirmedDeal)
    {
        logln((new StringBuilder("DEAL CONFIRMED: ")).append(confirmedDeal.getID()).toString(), true);
    }

    String timeString;
    String runFolderPath;
    String fileName;
    String filePath;
    public boolean enabled;
}
