// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContractAction.java

package ddejonge.nb3.domain.contract;

import ddejonge.nb3.algorithm.Nb3AgentSet;
import ddejonge.nb3.domain.Nb3Action;

// Referenced classes of package ddejonge.nb3.domain.contract:
//            ContractInstanceInfo

public class ContractAction extends Nb3Action
{

    public ContractAction(int myAgentNumber, int otherAgentNumber, int _issue, int _value)
    {
        int numVals = ContractInstanceInfo.getNumValuesOfIssue(_issue);
        if(_value >= numVals)
        {
            throw new IndexOutOfBoundsException((new StringBuilder("Issue ")).append(issue).append("only has ").append(numVals).append(" possible values.").toString());
        } else
        {
            issue = _issue;
            value = _value;
            participatingAgents.add(otherAgentNumber);
            participatingAgents.add(myAgentNumber);
            return;
        }
    }

    protected void setPA()
    {
    }

    public int getIssue()
    {
        return issue;
    }

    public int getValue()
    {
        return value;
    }

    int issue;
    int value;
}
