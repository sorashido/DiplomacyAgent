// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Proposal.java

package ddejonge.nb3.algorithm;

import ddejonge.nb3.domain.*;
import ddejonge.nb3.tree.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package ddejonge.nb3.algorithm:
//            Nb3AgentSet, Nb3Algorithm

public class Nb3Proposal
    implements Comparable
{

    protected Nb3Proposal()
    {
        discoverTime = -1;
        normalizedUtility = new float[Nb3InstanceInfo.numAgents];
        e = new float[Nb3InstanceInfo.numAgents];
        myNormalizedUtility = -666F;
        oppNormalizedUtility = -1F;
        actions = new ArrayList(10);
        pa = new Nb3AgentSet();
        waitingFor = new Nb3AgentSet();
        isValid = true;
        isAcceptedByMe = false;
        confirmationTime = -1;
    }

    public Nb3Proposal(Nb3Node n, int discTime, int proposer)
    {
        discoverTime = -1;
        normalizedUtility = new float[Nb3InstanceInfo.numAgents];
        e = new float[Nb3InstanceInfo.numAgents];
        myNormalizedUtility = -666F;
        oppNormalizedUtility = -1F;
        actions = new ArrayList(10);
        pa = new Nb3AgentSet();
        waitingFor = new Nb3AgentSet();
        isValid = true;
        isAcceptedByMe = false;
        confirmationTime = -1;
        creationTime = Nb3InstanceInfo.getCurrentTime();
        discoverTime = discTime;
        proposalsMade++;
        id = (new StringBuilder(String.valueOf(Nb3InstanceInfo.agentNames[proposer]))).append(proposalsMade).toString();
        correspondingNodeId = n.nodeId;
        for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
        {
            normalizedUtility[i] = n.getNormalizedUtility(i);
            e[i] = n.e[i];
        }

        myNormalizedUtility = n.getMyNormalizedUtility();
        oppNormalizedUtility = n.getOpponentUtility();
        if(myNormalizedUtility == -666F)
            throw new IllegalArgumentException("Nb3Proposal.Nb3Proposal() Error! normalized utilities have not been calculated.");
        actions.addAll(n.theTree.theAlgorithm.labels2Actions(n.getPath()));
        pa.addAll(n.theTree.theAlgorithm.getParticipatingAgents(n.getPath()));
        if(actions.size() == 0)
        {
            ArrayList labels = n.getPath();
            System.out.println(labels);
        }
        waitingFor.addAll(pa);
        waitingFor.remove(proposer);
    }

    public Nb3Proposal(Nb3IncomingProposalNode ipn)
    {
        discoverTime = -1;
        normalizedUtility = new float[Nb3InstanceInfo.numAgents];
        e = new float[Nb3InstanceInfo.numAgents];
        myNormalizedUtility = -666F;
        oppNormalizedUtility = -1F;
        actions = new ArrayList(10);
        pa = new Nb3AgentSet();
        waitingFor = new Nb3AgentSet();
        isValid = true;
        isAcceptedByMe = false;
        confirmationTime = -1;
        creationTime = Nb3InstanceInfo.getCurrentTime();
        proposalsMade++;
        proposer = ipn.getProposer();
        id = ipn.proposalID;
        correspondingNodeId = ipn.nodeId;
        for(int i = 0; i < Nb3InstanceInfo.numAgents; i++)
        {
            normalizedUtility[i] = ipn.getNormalizedUtility(i);
            e[i] = ipn.e[i];
        }

        myNormalizedUtility = ipn.getMyNormalizedUtility();
        oppNormalizedUtility = ipn.getOpponentUtility();
        actions.addAll(ipn.theTree.theAlgorithm.labels2Actions(ipn.getPath()));
        pa.addAll(ipn.theTree.theAlgorithm.getParticipatingAgents(ipn.getPath()));
        waitingFor.addAll(pa);
        waitingFor.remove(proposer);
    }

    public Nb3Proposal(Nb3Message msg)
    {
        discoverTime = -1;
        normalizedUtility = new float[Nb3InstanceInfo.numAgents];
        e = new float[Nb3InstanceInfo.numAgents];
        myNormalizedUtility = -666F;
        oppNormalizedUtility = -1F;
        actions = new ArrayList(10);
        pa = new Nb3AgentSet();
        waitingFor = new Nb3AgentSet();
        isValid = true;
        isAcceptedByMe = false;
        confirmationTime = -1;
        creationTime = Nb3InstanceInfo.getCurrentTime();
        proposer = msg.getSender();
        id = msg.getConversationId();
        actions = msg.getActions();
        for(Iterator iterator = actions.iterator(); iterator.hasNext();)
        {
            Nb3Action ac = (Nb3Action)iterator.next();
            if(ac.getParticipatingAgents() != null)
                pa.addAll(ac.getParticipatingAgents());
        }

        waitingFor.addAll(pa);
        waitingFor.remove(proposer);
    }

    public ArrayList getActions()
    {
        return actions;
    }

    public Nb3AgentSet getParticipatingAgents()
    {
        return pa;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public String getID()
    {
        return id;
    }

    public int getProposer()
    {
        return proposer;
    }

    public String toMessageString()
    {
        String string = ((Nb3Action)actions.get(0)).toString();
        for(int i = 1; i < actions.size(); i++)
            string = (new StringBuilder(String.valueOf(string))).append("#").append(((Nb3Action)actions.get(i)).toString()).toString();

        string = (new StringBuilder(String.valueOf(string))).append("#dt").append(discoverTime).toString();
        return string;
    }

    public void setAcceptedBy(int acceptingAgent)
    {
        waitingFor.remove(acceptingAgent);
    }

    public boolean isAccepted()
    {
        return waitingFor.isEmpty();
    }

    public double getMyNormalizedUtility()
    {
        return (double)myNormalizedUtility;
    }

    public double getOppNormalizedUtility()
    {
        return (double)oppNormalizedUtility;
    }

    public String toString()
    {
        String string = ((Nb3Action)actions.get(0)).toString();
        for(int i = 1; i < actions.size(); i++)
            string = (new StringBuilder(String.valueOf(string))).append("\r\n").append(((Nb3Action)actions.get(i)).toString()).toString();

        return string;
    }

    public int compareTo(Nb3Proposal other)
    {
        if(myNormalizedUtility > other.myNormalizedUtility)
            return -1;
        return myNormalizedUtility >= other.myNormalizedUtility ? 0 : 1;
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((Nb3Proposal)obj);
    }

    static int proposalsMade = 0;
    int creationTime;
    public int discoverTime;
    private int proposer;
    private String id;
    public int correspondingNodeId;
    public float normalizedUtility[];
    public float e[];
    public float myNormalizedUtility;
    public float oppNormalizedUtility;
    private ArrayList actions;
    private Nb3AgentSet pa;
    public Nb3AgentSet waitingFor;
    public boolean isValid;
    public boolean isAcceptedByMe;
    public int confirmationTime;

}
