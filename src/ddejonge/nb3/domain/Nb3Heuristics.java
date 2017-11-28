// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Heuristics.java

package ddejonge.nb3.domain;

import ddejonge.nb3.algorithm.Nb3AgentSet;
import ddejonge.nb3.algorithm.Nb3Algorithm;
import ddejonge.nb3.tree.Nb3Node;
import ddejonge.nb3.tree.Nb3Tree;
import java.util.*;

// Referenced classes of package ddejonge.nb3.domain:
//            Nb3WorldState

public abstract class Nb3Heuristics
{

    public Nb3Heuristics()
    {
    }

    public abstract void calculateBounds(Nb3Node nb3node, ArrayList arraylist, Nb3WorldState nb3worldstate);

    public float calculateGlobalUpperBound(int agent, Nb3WorldState currentState)
    {
        return nb3Algorithm.theTree.root.ub[agent];
    }

    public float calculateGlobalLowerBound(int agent, Nb3WorldState currentState)
    {
        return nb3Algorithm.theTree.root.lb[agent];
    }

    public void calculateExpansionHeuristic(Nb3Node node)
    {
        float eh = 1.0F;
        Nb3AgentSet pa = nb3Algorithm.getParticipatingAgents(node.getPath());
        Arrays.fill(node.prob, -1F);
        for(Iterator iterator = pa.iterator(); iterator.hasNext();)
        {
            int ag = ((Integer)iterator.next()).intValue();
            if(ag != nb3Algorithm.myAgentNumber)
            {
                node.prob[ag] = calculateProbability(node, ag);
                eh *= node.prob[ag];
            }
        }

        eh *= calculateExpectedUtility(node, nb3Algorithm.myAgentNumber);
        node.setExpansionHeuristic(eh);
    }

    public float calculateProbability(Nb3Node node, int ag)
    {
        float off = nb3Algorithm.theTree.offer[ag];
        float rv = nb3Algorithm.theTree.rv[ag];
        float lb = node.lb[ag];
        float ub = node.ub[ag];
        if((double)Math.abs(ub - lb) < 0.01D)
        {
            if(lb >= off)
                return 1.0F;
            if(ub <= rv)
                return 0.0F;
            else
                return (ub - rv) / (off - rv);
        }
        float triangle_underBound = Math.max(lb, rv);
        float triangle_upperBound = Math.min(ub, off);
        float square_underBound = Math.max(off, lb);
        float square_upperBound = ub;
        float triangleArea;
        if(triangle_underBound >= triangle_upperBound)
        {
            triangleArea = 0.0F;
        } else
        {
            float triangleWidth = triangle_upperBound - triangle_underBound;
            float centerOfTriangleRange = (triangle_underBound + triangle_upperBound) / 2.0F;
            float valueAtCenterOfTriangle = (centerOfTriangleRange - rv) / (off - rv);
            triangleArea = triangleWidth * valueAtCenterOfTriangle;
        }
        float squareArea;
        if(square_underBound >= square_upperBound)
            squareArea = 0.0F;
        else
            squareArea = square_upperBound - square_underBound;
        float prob = (squareArea + triangleArea) / (ub - lb);
        return prob;
    }

    float calculateExpectedUtility(Nb3Node node, int ag)
    {
        float retVal = node.ub[ag] - node.theTree.rv[ag];
        retVal *= retVal;
        retVal /= node.ub[ag] - node.lb[ag];
        return retVal;
    }

    public void reCalculateExpansionHeuristic(Nb3Node node, int agent)
    {
        if(agent == nb3Algorithm.myAgentNumber)
        {
            throw new ArithmeticException();
        } else
        {
            float eh = node.getExpansionHeuristic() / node.prob[agent];
            eh *= calculateProbability(node, agent);
            node.setExpansionHeuristic(eh);
            return;
        }
    }

    public void setOwner(Nb3Algorithm algo)
    {
        nb3Algorithm = algo;
    }

    public Nb3Algorithm nb3Algorithm;
}
