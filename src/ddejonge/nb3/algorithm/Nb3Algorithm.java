// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Algorithm.java

package ddejonge.nb3.algorithm;

import ddejonge.nb3.domain.*;
import ddejonge.nb3.tools.CircularList;
import ddejonge.nb3.tools.MyTreeViewer;
import ddejonge.nb3.tree.*;
import java.util.*;

// Referenced classes of package ddejonge.nb3.algorithm:
//            Nb3Logger, Nb3Proposal, Nb3AgentSet

public abstract class Nb3Algorithm
{

    public int getNumFoundByUs()
    {
        return numFoundByUs;
    }

    public int getNumProposedToUs()
    {
        return numProposedToUs;
    }

    public int getNumProposedByUs()
    {
        return numProposedByUs;
    }

    public int getNumAcceptedByUs()
    {
        return numAcceptedByUs;
    }

    public int getNumConfirmed()
    {
        return confirmed.size();
    }

    public Nb3Algorithm(Nb3Heuristics heurs, int myAgentNumber, boolean minimize, Nb3WorldState initialState)
    {
        myAgentName = "";
        allProposals = new HashMap(30000);
        theTree = null;
        foundByUs = new PriorityQueue(1000);
        proposedToUs = new LinkedList();
        proposedByUs = new LinkedList();
        confirmed = new ArrayList(100);
        disccarded = new ArrayList(100);
        numNodes = 0;
        typeOfNodeToSplit = -1;
        numFoundByUs = 0;
        numProposedToUs = 0;
        numProposedByUs = 0;
        numAcceptedByUs = 0;
        mtv = null;
        status = "--";
        cl = new CircularList();
        theHeuristics = heurs;
        heurs.setOwner(this);
        this.myAgentNumber = myAgentNumber;
        myAgentName = Nb3InstanceInfo.agentNames[myAgentNumber];
        this.initialState = initialState;
        currentState = initialState.copy();
        Nb3Tree.original_gub = null;
        Nb3Tree.original_rv = null;
        MINIMIZE = minimize;
    }

    public void setNegoLogger(Nb3Logger logger)
    {
        nego_logger = logger;
    }

    public void setSearchLogger(Nb3Logger logger)
    {
        search_logger = logger;
    }

    public void initialize(long endTime)
    {
        initialize(new Nb3NodeQueue(), getRootLabel(), endTime);
    }

    public void initialize(Nb3NodeQueue queue, long endTime)
    {
        initialize(queue, getRootLabel(), endTime);
    }

    public void initialize(Nb3NodeQueue queue, Nb3Label rootLabel, long _endTime)
    {
        Nb3InstanceInfo.startTime = System.currentTimeMillis();
        Nb3InstanceInfo.endTime = _endTime;
        if(theTree == null)
            theTree = new Nb3Tree(this, queue, rootLabel);
        else
            theTree.clear();
        theHeuristics.calculateBounds(theTree.root, theTree.root.getPath(), currentState);
        for(int ag = 0; ag < Nb3InstanceInfo.numAgents; ag++)
        {
            theTree.gub[ag] = theTree.root.ub[ag];
            theTree.rv[ag] = theTree.root.e[ag];
            theTree.glb[ag] = theTree.root.lb[ag];
            theTree.offer[ag] = theTree.gub[ag];
        }

        theHeuristics.calculateExpansionHeuristic(theTree.root);
        if(Nb3Tree.original_gub == null)
        {
            Nb3Tree.original_gub = new float[Nb3InstanceInfo.numAgents];
            for(int j = 0; j < Nb3InstanceInfo.numAgents; j++)
                Nb3Tree.original_gub[j] = theTree.gub[j];

            Nb3Tree.original_rv = new float[Nb3InstanceInfo.numAgents];
            for(int j = 0; j < Nb3InstanceInfo.numAgents; j++)
                Nb3Tree.original_rv[j] = theTree.rv[j];

        }
        search_logger.logNode(theTree.root);
        search_logger.logBounds(theTree.root);
    }

    protected abstract Nb3Label getRootLabel();

    public void expand()
    {
        Nb3Node nodeToSplit = chooseNode();
        List splitLabels = getSplitLabels(nodeToSplit);
        splitNode(nodeToSplit, splitLabels);
        if(nodeToSplit != null)
            search_logger.logln((new StringBuilder("Nb3Algorithm.expand() Number of rational deals found: ")).append(getNumFoundByUs()).toString());
    }

    private Nb3Node chooseNode()
    {
        Nb3NodeQueue que = theTree.queue;
        typeOfNodeToSplit = getTypeOfNodeToSplit();
        Nb3Node maxNode;
        do
        {
            maxNode = que.poll(typeOfNodeToSplit);
            if(maxNode == null)
                return null;
        } while(!maxNode.isOpened);
        search_logger.logChosenNode("Nb3Algorithm.chooseNode()", maxNode);
        return maxNode;
    }

    protected int getTypeOfNodeToSplit()
    {
        return 0;
    }

    protected abstract List getSplitLabels(Nb3Node nb3node);

    public abstract Nb3AgentSet getParticipatingAgents(ArrayList arraylist);

    private void splitNode(Nb3Node nodeToSplit, List labels)
    {
        timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
        cl.add((new StringBuilder("splitNode() 1. ")).append(timeLeft).toString());
        if(nodeToSplit == null)
        {
            search_logger.logNoNodeToSplit();
            return;
        }
        if(labels == null || labels.size() == 0)
        {
            theTree.closeNode(nodeToSplit);
            search_logger.logNoLabelToSplit(nodeToSplit);
            return;
        }
        timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
        cl.add((new StringBuilder("splitNode() 2. ")).append(timeLeft).toString());
        theTree.selectNode(nodeToSplit);
        theTree.closeNode(nodeToSplit);
        timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
        cl.add((new StringBuilder("splitNode() 3. ")).append(timeLeft).toString());
        for(Iterator iterator = labels.iterator(); iterator.hasNext(); cl.add((new StringBuilder("splitNode() 10. ")).append(timeLeft).toString()))
        {
            Nb3Label label = (Nb3Label)iterator.next();
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
            cl.add((new StringBuilder("splitNode() 4. ")).append(timeLeft).toString());
            Nb3Node n = new Nb3Node(label);
            theTree.addNode(n);
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
            cl.add((new StringBuilder("splitNode() 5. ")).append(timeLeft).toString());
            if(System.currentTimeMillis() >= Nb3InstanceInfo.endTime)
                return;
            theHeuristics.calculateBounds(n, n.getPath(), currentState);
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
            cl.add((new StringBuilder("splitNode() 6. ")).append(timeLeft).toString());
            if(System.currentTimeMillis() >= Nb3InstanceInfo.endTime)
                return;
            theHeuristics.calculateExpansionHeuristic(n);
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
            cl.add((new StringBuilder("splitNode() 7. ")).append(timeLeft).toString());
            storeProposalIfRational(n, myAgentNumber, null);
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
            cl.add((new StringBuilder("splitNode() 8. ")).append(timeLeft).toString());
            theTree.queue.add(n);
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
            cl.add((new StringBuilder("splitNode() 9. ")).append(timeLeft).toString());
            search_logger.logNode(n);
            search_logger.logBounds(n);
            if(n.label.canBeProposed() && isRational(n, true))
                search_logger.logln("RATIONAL");
            timeLeft = Nb3InstanceInfo.endTime - System.currentTimeMillis();
        }

    }

    protected Nb3Proposal storeProposalIfRational(Nb3Node n, int proposer, String id)
    {
        if(!n.label.canBeProposed())
            return null;
        boolean isFoundByUs = proposer == myAgentNumber;
        boolean rational = isRational(n, isFoundByUs);
        if(!rational && isFoundByUs)
            return null;
        int discoverTime = Nb3InstanceInfo.getCurrentTime();
        Nb3Proposal newProp = new Nb3Proposal(n, discoverTime, proposer);
        if(id != null)
            newProp.setID(id);
        allProposals.put(newProp.getID(), newProp);
        if(rational)
            if(isFoundByUs)
            {
                foundByUs.add(newProp);
                numFoundByUs++;
            } else
            {
                proposedToUs.add(newProp);
                numProposedToUs++;
            }
        return newProp;
    }

    public void handleIncomingMessages(Nb3Message msg)
    {
        switch($SWITCH_TABLE$ddejonge$nb3$domain$Nb3InstanceInfo$MsgType()[msg.getPerformative().ordinal()])
        {
        default:
            break;

        case 1: // '\001'
            handleIncomingProposeMessage(msg);
            break;

        case 2: // '\002'
            handleIncomingAcceptMessage(msg);
            break;

        case 5: // '\005'
            nego_logger.logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" Received a rejection of proposal ").append(msg.getConversationId()).toString());
            Nb3Proposal rejectedProp = (Nb3Proposal)allProposals.get(msg.getConversationId());
            if(!foundByUs.remove(rejectedProp))
                proposedByUs.remove(rejectedProp);
            break;

        case 3: // '\003'
            nego_logger.logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" Received a refusal of proposal ").append(msg.getConversationId()).toString());
            Nb3Proposal refusedProp = (Nb3Proposal)allProposals.get(msg.getConversationId());
            proposedByUs.remove(refusedProp);
            proposedToUs.remove(refusedProp);
            break;

        case 4: // '\004'
            nego_logger.logSeparationLineHead();
            nego_logger.logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" ***Received a CONFIRMATION of proposal ").append(msg.getConversationId()).toString(), true);
            Nb3Proposal confirmedProp = (Nb3Proposal)allProposals.get(msg.getConversationId());
            if(confirmedProp == null)
                confirmedProp = new Nb3Proposal(msg);
            handleDealConfirmation(confirmedProp);
            nego_logger.logSeparationLineFoot();
            break;
        }
    }

    public abstract ArrayList actions2Labels(ArrayList arraylist);

    public abstract List labels2Actions(List list);

    protected void updateOffers(float newOfferVal, int idOfAgentToUpdate)
    {
        if(newOfferVal > theTree.offer[idOfAgentToUpdate])
        {
            theTree.offer[idOfAgentToUpdate] = newOfferVal;
            nego_logger.logln((new StringBuilder("updating offer level of: ")).append(idOfAgentToUpdate).toString());
            theTree.reorderQueue(idOfAgentToUpdate);
        }
    }

    protected void handleDealConfirmation(Nb3Proposal confirmedProp)
    {
        proposedByUs.remove(confirmedProp);
        proposedToUs.remove(confirmedProp);
        confirmed.add(confirmedProp);
        ArrayList actions = confirmedProp.getActions();
        ArrayList labels = actions2Labels(actions);
        nego_logger.logDealConfirmation(confirmedProp);
        currentState.update(actions);
        Nb3Node newRoot = addLabelsToTree(labels);
        theTree.resetRoot(newRoot);
        foundByUs.clear();
        proposedToUs.clear();
        proposedByUs.clear();
    }

    protected void handleDealConfirmation1_2(Nb3Proposal confirmedProp)
    {
        currentState.update(confirmedProp.getActions());
        initialize(Nb3InstanceInfo.endTime);
        foundByUs = new PriorityQueue(1000);
        proposedToUs = new LinkedList();
        proposedByUs = new LinkedList();
    }

    protected void handleIncomingProposeMessage(Nb3Message msg)
    {
        ArrayList actions = msg.getActions();
        if(!currentState.isLegal(actions))
        {
            nego_logger.logIllegalProposalReceived(myAgentName, msg);
            return;
        }
        nego_logger.logProposalReceived(myAgentName, msg);
        ArrayList labels = actions2Labels(actions);
        Nb3Node incomingProposalNode = addLabelsToTree(labels);
        if(incomingProposalNode == null)
            return;
        nego_logger.logIncomingProposalNode(incomingProposalNode);
        int proposer = msg.getSender();
        Nb3Proposal incomingProposal = storeProposalIfRational(incomingProposalNode, proposer, msg.getConversationId());
        if(incomingProposal != null)
            nego_logger.logln((new StringBuilder("Still waiting for: ")).append(incomingProposal.waitingFor).toString());
        updateOffers(incomingProposalNode.e[proposer], proposer);
    }

    protected void handleIncomingAcceptMessage(Nb3Message msg)
    {
        Nb3Proposal acceptedProp = (Nb3Proposal)allProposals.get(msg.getConversationId());
        int accepter = msg.getSender();
        if(acceptedProp != null)
        {
            acceptedProp.setAcceptedBy(accepter);
            updateOffers(acceptedProp.e[accepter], accepter);
            nego_logger.logIncomingAcceptance(acceptedProp, accepter);
        } else
        {
            nego_logger.logln((new StringBuilder(String.valueOf(Nb3InstanceInfo.getCurrentTime()))).append(" received accept message for unknown proposal ").append(msg.getConversationId()).toString());
        }
    }

    public Nb3Node addLabelsToTree(ArrayList labels)
    {
        Nb3Node currentNode = theTree.root;
        for(Iterator iterator = labels.iterator(); iterator.hasNext();)
        {
            Nb3Label lab = (Nb3Label)iterator.next();
            List children = currentNode.getChildren();
            boolean matchingLabelExists = false;
            for(Iterator iterator1 = children.iterator(); iterator1.hasNext();)
            {
                Nb3Node child = (Nb3Node)iterator1.next();
                if(lab.equals(child.label))
                {
                    matchingLabelExists = true;
                    currentNode = child;
                    break;
                }
            }

            if(!matchingLabelExists)
            {
                Nb3Node newChild = new Nb3Node(lab);
                theTree.selectNode(currentNode);
                theTree.addNode(newChild);
                if(System.currentTimeMillis() >= Nb3InstanceInfo.endTime)
                {
                    search_logger.logln((new StringBuilder("BREAKING FROM addLabelsToTree() current time: ")).append(System.currentTimeMillis()).append(" deadline: ").append(Nb3InstanceInfo.endTime).toString());
                    return null;
                }
                theHeuristics.calculateBounds(newChild, newChild.getPath(), currentState);
                if(System.currentTimeMillis() >= Nb3InstanceInfo.endTime)
                {
                    search_logger.logln((new StringBuilder("BREAKING FROM addLabelsToTree() current time: ")).append(System.currentTimeMillis()).append(" deadline: ").append(Nb3InstanceInfo.endTime).toString());
                    return null;
                }
                theHeuristics.calculateExpansionHeuristic(newChild);
                if(newChild.getExpansionHeuristic() > 0.0F)
                    theTree.queue.add(newChild);
                search_logger.logNode(newChild);
                search_logger.logBounds(newChild);
                if(newChild.label.canBeProposed() && isRational(newChild, false))
                    search_logger.logln("RATIONAL");
                currentNode = newChild;
            }
        }

        return currentNode;
    }

    public void acceptOrPropose()
    {
        calculateOppAsp();
        calculateMyAsp();
        Nb3Proposal bestFoundByUs = selectBestFoundByUs();
        Nb3Proposal bestProposedToUs = selectBestProposedToUs();
        if(bestProposedToUs == null && bestFoundByUs == null)
        {
            nego_logger.logln((new StringBuilder("Decision: WAIT, at time ")).append(Nb3InstanceInfo.getCurrentTime()).append(" Currently none of the deals found by us or proposed to us is selfish enough. myAsp: ").append(my_asp).append(" oppAsp: ").append(opp_asp).toString());
            return;
        }
        nego_logger.logBestProposals(bestFoundByUs, bestProposedToUs, my_asp, opp_asp);
        if(oursIsBetter(bestFoundByUs, bestProposedToUs))
        {
            propose(bestFoundByUs);
            nego_logger.logln();
            nego_logger.logln("Nb3Algorithm.acceptOrPropose()");
            nego_logger.logProposalMade(bestFoundByUs, myAgentNumber, Nb3Tree.original_gub[myAgentNumber], my_asp, opp_asp);
            return;
        } else
        {
            accept(bestProposedToUs);
            nego_logger.logAcceptedProposal(bestProposedToUs);
            return;
        }
    }

    protected boolean oursIsBetter(Nb3Proposal found, Nb3Proposal proposed)
    {
        if(found == null)
            return false;
        if(proposed == null)
            return true;
        if(found.getOppNormalizedUtility() < opp_asp && proposed.getOppNormalizedUtility() > opp_asp)
            return false;
        return found.getMyNormalizedUtility() > proposed.getMyNormalizedUtility();
    }

    private Nb3Proposal selectBestFoundByUs()
    {
        Nb3Proposal mostSelfish = null;
        Nb3Proposal mostAltruisticAndSelfishEnough = null;
        mostSelfish = (Nb3Proposal)foundByUs.peek();
        if(mostSelfish == null || (double)mostSelfish.normalizedUtility[myAgentNumber] < my_asp)
            return null;
        if(mostSelfish.getOppNormalizedUtility() >= opp_asp)
            return mostSelfish;
        double max_val = 0.0D;
        for(Iterator iterator = foundByUs.iterator(); iterator.hasNext();)
        {
            Nb3Proposal proposal = (Nb3Proposal)iterator.next();
            if((double)proposal.normalizedUtility[myAgentNumber] >= my_asp)
            {
                double op_norm_util = proposal.getOppNormalizedUtility();
                if(op_norm_util > max_val)
                {
                    max_val = op_norm_util;
                    mostAltruisticAndSelfishEnough = proposal;
                }
            }
        }

        return mostAltruisticAndSelfishEnough;
    }

    protected Nb3Proposal selectBestProposedToUs()
    {
        float max = 0.0F;
        Nb3Proposal bestProposedToUs = null;
        for(Iterator iterator = proposedToUs.iterator(); iterator.hasNext();)
        {
            Nb3Proposal prop = (Nb3Proposal)iterator.next();
            if(!prop.isAcceptedByMe && prop.normalizedUtility[myAgentNumber] > max)
            {
                max = prop.normalizedUtility[myAgentNumber];
                bestProposedToUs = prop;
            }
        }

        if((double)max >= my_asp)
            return bestProposedToUs;
        else
            return null;
    }

    private Nb3Proposal _selectBestFoundByUs()
    {
        Nb3Proposal mostSelfish = null;
        boolean isAcceptable = false;
        double max_val = 0.0D;
        Nb3Proposal most_altruistic_prop = null;
        calculateOppAsp();
        calculateMyAsp();
        while(!isAcceptable) 
        {
            mostSelfish = (Nb3Proposal)foundByUs.peek();
            if(mostSelfish == null)
                if(most_altruistic_prop != null && (double)most_altruistic_prop.normalizedUtility[myAgentNumber] > my_asp)
                    return most_altruistic_prop;
                else
                    return null;
            double op_norm_util = mostSelfish.getOppNormalizedUtility();
            if(op_norm_util > max_val)
            {
                max_val = op_norm_util;
                most_altruistic_prop = mostSelfish;
            }
            if(op_norm_util <= opp_asp)
            {
                foundByUs.poll();
                disccarded.add(mostSelfish);
            } else
            {
                isAcceptable = true;
            }
        }
        return mostSelfish;
    }

    protected Nb3Proposal _selectBestProposedToUs()
    {
        float max_e = 0.0F;
        Nb3Proposal bestProposedToUs = null;
        for(Iterator iterator = proposedToUs.iterator(); iterator.hasNext();)
        {
            Nb3Proposal prop = (Nb3Proposal)iterator.next();
            if(!prop.isAcceptedByMe && prop.e[myAgentNumber] > max_e && prop.e[myAgentNumber] > theTree.rv[myAgentNumber])
            {
                max_e = prop.e[myAgentNumber];
                bestProposedToUs = prop;
            }
        }

        return bestProposedToUs;
    }

    protected void propose(Nb3Proposal nextProposal)
    {
        proposeDeal(nextProposal);
        numProposedByUs++;
        foundByUs.remove(nextProposal);
        proposedByUs.add(nextProposal);
    }

    protected abstract void proposeDeal(Nb3Proposal nb3proposal);

    protected void accept(Nb3Proposal nextProposal)
    {
        if(nextProposal.isAcceptedByMe)
        {
            return;
        } else
        {
            nextProposal.isAcceptedByMe = true;
            nextProposal.setAcceptedBy(myAgentNumber);
            numAcceptedByUs++;
            acceptDeal(nextProposal);
            return;
        }
    }

    protected abstract void acceptDeal(Nb3Proposal nb3proposal);

    protected void calculateOppAsp()
    {
        status = "in Nb3Algorithm.calculateOppAsp()";
        double t = Nb3InstanceInfo.getCurrentTime();
        double t_d = Nb3InstanceInfo.endTime - Nb3InstanceInfo.startTime;
        double z = 0.98999999999999999D;
        double a = 4D;
        opp_asp = z / (Math.exp(-a) - 1.0D);
        opp_asp *= Math.exp((-a * t) / t_d) - 1.0D;
    }

    protected void calculateMyAsp()
    {
        double t = Nb3InstanceInfo.getCurrentTime();
        double t_d = Nb3InstanceInfo.endTime - Nb3InstanceInfo.startTime;
        double z = 0.98999999999999999D;
        double a = 2D;
        my_asp = z / (Math.exp(-a) - 1.0D);
        my_asp *= Math.exp((-a * t) / t_d) - 1.0D;
        my_asp = 1.0D - my_asp;
        if(my_asp < theTree.getMyCurrentNormalizedUtility())
            my_asp = theTree.getMyCurrentNormalizedUtility();
    }

    public boolean checkTime()
    {
        return System.currentTimeMillis() < Nb3InstanceInfo.endTime;
    }

    public boolean isRational(Nb3Node n, boolean isFoundByUs)
    {
        if(isFoundByUs)
        {
            Nb3AgentSet pa = getParticipatingAgents(n.getPath());
            for(Iterator iterator = pa.iterator(); iterator.hasNext();)
            {
                int ag = ((Integer)iterator.next()).intValue();
                if((double)n.e[ag] - 0.10000000000000001D <= (double)theTree.rv[ag])
                    return false;
            }

        }
        return (double)n.e[myAgentNumber] - 0.10000000000000001D > (double)theTree.rv[myAgentNumber];
    }

    static int[] $SWITCH_TABLE$ddejonge$nb3$domain$Nb3InstanceInfo$MsgType()
    {
        $SWITCH_TABLE$ddejonge$nb3$domain$Nb3InstanceInfo$MsgType;
        if($SWITCH_TABLE$ddejonge$nb3$domain$Nb3InstanceInfo$MsgType == null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        JVM INSTR pop ;
        int ai[] = new int[ddejonge.nb3.domain.Nb3InstanceInfo.MsgType.values().length];
        try
        {
            ai[ddejonge.nb3.domain.Nb3InstanceInfo.MsgType.ACCEPT.ordinal()] = 2;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[ddejonge.nb3.domain.Nb3InstanceInfo.MsgType.CONFIRM.ordinal()] = 4;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[ddejonge.nb3.domain.Nb3InstanceInfo.MsgType.PROPOSE.ordinal()] = 1;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[ddejonge.nb3.domain.Nb3InstanceInfo.MsgType.REFUSE.ordinal()] = 3;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[ddejonge.nb3.domain.Nb3InstanceInfo.MsgType.REJECT.ordinal()] = 5;
        }
        catch(NoSuchFieldError _ex) { }
        return $SWITCH_TABLE$ddejonge$nb3$domain$Nb3InstanceInfo$MsgType = ai;
    }

    public Nb3Logger search_logger;
    public Nb3Logger nego_logger;
    public boolean MINIMIZE;
    public Nb3WorldState initialState;
    public Nb3WorldState currentState;
    public Nb3Heuristics theHeuristics;
    public int myAgentNumber;
    public String myAgentName;
    public HashMap allProposals;
    public Nb3Tree theTree;
    ArrayList splitActions;
    public PriorityQueue foundByUs;
    public LinkedList proposedToUs;
    protected LinkedList proposedByUs;
    public ArrayList confirmed;
    protected ArrayList disccarded;
    protected double opp_asp;
    final double deadline_level = 0.98999999999999999D;
    final double concession_degree = 4D;
    public double my_asp;
    final double deadline_level2 = 0.01D;
    final double concession_degree2 = 2D;
    public int numNodes;
    protected int typeOfNodeToSplit;
    protected int numFoundByUs;
    protected int numProposedToUs;
    protected int numProposedByUs;
    protected int numAcceptedByUs;
    MyTreeViewer mtv;
    public String status;
    public CircularList cl;
    long timeLeft;
    private static int $SWITCH_TABLE$ddejonge$nb3$domain$Nb3InstanceInfo$MsgType[];
}
