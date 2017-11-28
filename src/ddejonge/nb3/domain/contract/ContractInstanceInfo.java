// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContractInstanceInfo.java

package ddejonge.nb3.domain.contract;

import ddejonge.nb3.domain.Nb3InstanceInfo;

public class ContractInstanceInfo extends Nb3InstanceInfo
{

    public ContractInstanceInfo()
    {
    }

    public static void setIssues(int issues[])
    {
        numValuesOfIssue = issues;
        numIssues = numValuesOfIssue.length;
    }

    public static void setNumValuesOfIssue(int i, int num)
    {
        numValuesOfIssue[i] = num;
    }

    public static int getNumValuesOfIssue(int i)
    {
        return numValuesOfIssue[i];
    }

    public static int getNumIssues()
    {
        return numIssues;
    }

    protected static int numIssues;
    private static int numValuesOfIssue[];
}
