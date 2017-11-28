// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DBraneValueCalculator.java

package ddejonge.dBrane_1_1;

import ddejonge.dBrane_1_1.andOrSearch.ValueCalculator;
import ddejonge.dBrane_1_1.andOrSearch.Variable;
import ddejonge.dBrane_1_1.andOrSearch.VariableCluster;
import ddejonge.dipgameExtensions.DipUtils;
import ddejonge.dipgameExtensions.ProvinceSet;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.Order;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            OrderContainer, BattlePlan, DoubleBattlePlan, CompatibilityChecker

public class DBraneValueCalculator
    implements ValueCalculator
{

    public DBraneValueCalculator(int varId2scID[], int numVars, BattlePlan _sc2Agreement[], boolean _isInvincibleAgreement[])
    {
        branchPlans = new ArrayList();
        ordersInBranch = new ArrayList();
        ordersInBranchByLocation = new Order[120];
        subsetFound = false;
        provincesToSkip = new ProvinceSet();
        this.varId2scID = varId2scID;
        this.numVars = numVars;
        sc2Agreement = _sc2Agreement;
        isInvincibleAgreement = _isInvincibleAgreement;
    }

    public void setClusterTrees(List rootClusters)
    {
        this.rootClusters = rootClusters;
        varId2numVarsToCome = new int[numVars];
        for(List openList = new ArrayList(rootClusters); openList.size() > 0;)
        {
            VariableCluster current = (VariableCluster)openList.remove(openList.size() - 1);
            if(current.hasChildren())
            {
                Object child;
                for(Iterator iterator = current.getChildren().iterator(); iterator.hasNext(); openList.add((VariableCluster)child))
                    child = iterator.next();

            }
            int size = current.getVariables().size();
            for(VariableCluster higherCluster = current.getParent(); higherCluster != null; higherCluster = higherCluster.getParent())
            {
                for(Iterator iterator1 = higherCluster.getVariables().iterator(); iterator1.hasNext();)
                {
                    Variable v = (Variable)iterator1.next();
                    varId2numVarsToCome[v.getId()] += size;
                }

            }

            for(Iterator iterator2 = current.getVariables().iterator(); iterator2.hasNext();)
            {
                Variable v = (Variable)iterator2.next();
                size--;
                varId2numVarsToCome[v.getId()] += size;
            }

        }

    }

    public void setBranchLabels(Object branchLabels[])
    {
        subsetFound = false;
        provincesToSkip.clear();
        branchPlans.clear();
        ordersInBranch.clear();
        Arrays.fill(ordersInBranchByLocation, null);
        for(int i = 0; i < branchLabels.length; i++)
        {
            OrderContainer branchLabel = (OrderContainer)branchLabels[i];
            if(branchLabel != null)
            {
                branchPlans.add(branchLabel);
                if(branchLabel instanceof BattlePlan)
                {
                    int sc = varId2scID[i];
                    if(sc2Agreement[sc] == null || isInvincibleAgreement[sc])
                        provincesToSkip.add(sc);
                } else
                if(branchLabel instanceof DoubleBattlePlan)
                {
                    provincesToSkip.add(((DoubleBattlePlan)branchLabel).pp0.getTargetProvince());
                    provincesToSkip.add(((DoubleBattlePlan)branchLabel).pp1.getTargetProvince());
                } else
                {
                    throw new IllegalArgumentException((new StringBuilder("DBraneValueCalculator.consistent() Error! unknown class: ")).append(branchLabel.getClass().getName()).toString());
                }
                ordersInBranch.addAll(branchLabel.getAllOrders());
                for(Iterator iterator = branchLabel.getAllOrders().iterator(); iterator.hasNext();)
                {
                    Order o = (Order)iterator.next();
                    ordersInBranchByLocation[o.getLocation().getId()] = o;
                }

            }
        }

    }

    public void setNewChildLabel(int varIndex, OrderContainer childLabel)
    {
        consistent = determineConsistency(varIndex, childLabel);
        nodeValue = calculateNodeValue(varIndex, childLabel);
        int ubWithoutNodeValue = calculateUbWithoutNodeValue(varIndex);
        ub = ubWithoutNodeValue + nodeValue;
    }

    private boolean determineConsistency(int varIndex, OrderContainer childLabel)
    {
        if(childLabel == null)
            return !subsetFound;
        if(childLabel instanceof BattlePlan)
        {
            int sc = varId2scID[varIndex];
            if(sc2Agreement[sc] != null)
                if(sc2Agreement[sc] != childLabel)
                    throw new IllegalArgumentException("DBraneValueCalculator.determineConsistency() Error! we have a child label that does not equal the agreement, as it should.");
                else
                    return true;
            if(provincesToSkip.contains(sc))
                return false;
        } else
        if(childLabel instanceof DoubleBattlePlan)
        {
            Province target0 = ((DoubleBattlePlan)childLabel).pp0.getTargetProvince();
            if(provincesToSkip.contains(target0))
                return false;
            Province target1 = ((DoubleBattlePlan)childLabel).pp1.getTargetProvince();
            if(provincesToSkip.contains(target1))
                return false;
        } else
        {
            throw new IllegalArgumentException((new StringBuilder("DBraneValueCalculator.consistent() Error! unknown class: ")).append(childLabel.getClass().getName()).toString());
        }
        boolean isSubset = true;
        List childOrders = childLabel.getAllOrders();
        int size = childOrders.size();
        for(int i = 0; i < size; i++)
        {
            Order order = (Order)childOrders.get(i);
            Order branchOrder = ordersInBranchByLocation[order.getLocation().getId()];
            if(branchOrder != null && DipUtils.areEqual(branchOrder, order))
                continue;
            isSubset = false;
            break;
        }

        if(isSubset)
            if(subsetFound)
            {
                return false;
            } else
            {
                subsetFound = true;
                return true;
            }
        boolean comp = true;
        size = branchPlans.size();
        for(int i = 0; i < size; i++)
        {
            OrderContainer pp = (OrderContainer)branchPlans.get(i);
            comp = CompatibilityChecker.determineCompatibility(childLabel.getAllOrders(), pp.getAllOrders());
            if(!comp)
                break;
        }

        if(comp)
        {
            boolean noSelfCuts = CompatibilityChecker.hasNoSelfCuts(ordersInBranch, childLabel.getAllOrders());
            if(noSelfCuts)
                return true;
        }
        return false;
    }

    private int calculateNodeValue(int varIndex, OrderContainer childLabel)
    {
        if(childLabel == null)
            return 0;
        if(childLabel instanceof BattlePlan)
        {
            int sc = varId2scID[varIndex];
            return sc2Agreement[sc] == null || isInvincibleAgreement[sc] ? 100 : 0;
        }
        if(childLabel instanceof DoubleBattlePlan)
            return 101;
        else
            throw new IllegalArgumentException((new StringBuilder("DBraneValueCalculator.calculateValue() Error! unkonw class: ")).append(childLabel.getClass().getName()).toString());
    }

    private int calculateUbWithoutNodeValue(int varIndex)
    {
        return varId2numVarsToCome[varIndex] * 101;
    }

    public int calculateUB(VariableCluster cluster)
    {
        Variable lastVar = (Variable)cluster.getVariables().get(0);
        int lastVarId = lastVar.getId();
        int varsToCome = varId2numVarsToCome[lastVarId] + 1;
        return varsToCome * 101;
    }

    public boolean isConsistent()
    {
        return consistent;
    }

    public int getUB()
    {
        return ub;
    }

    public int getNodeValue()
    {
        return nodeValue;
    }

    public volatile void setNewChildLabel(int i, Object obj)
    {
        setNewChildLabel(i, (OrderContainer)obj);
    }

    static final int SINGLE_PLAN_VALUE = 100;
    static final int DOUBLE_PLAN_VALUE = 101;
    static final int MAX_VALUE = 101;
    int varId2scID[];
    int numVars;
    List rootClusters;
    private List branchPlans;
    private List ordersInBranch;
    private Order ordersInBranchByLocation[];
    private boolean subsetFound;
    boolean consistent;
    int nodeValue;
    int ub;
    int varId2numVarsToCome[];
    private ProvinceSet provincesToSkip;
    BattlePlan sc2Agreement[];
    boolean isInvincibleAgreement[];
}
