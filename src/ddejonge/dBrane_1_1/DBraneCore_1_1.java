// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DBraneCore_1_1.java

package ddejonge.dBrane_1_1;

import ddejonge.dBrane.tools.ArrayOfLists;
import ddejonge.dBrane_1_1.andOrSearch.AndOrTree;
import ddejonge.dBrane_1_1.andOrSearch.Graph;
import ddejonge.dipgameExtensions.*;
import ddejonge.nb3.tools.LongSet;
import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.orders.*;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            BattlePlan, BattlePlanStorage, CoalitionStructure, DBraneWorldState, 
//            CompatibilityChecker, DoubleBattlePlan, PlanListWithValue, OrderContainer, 
//            FullPlanIterator, DBraneValueCalculator, ForceCalculator, DBrane_1_1

public class DBraneCore_1_1
{

    DBraneCore_1_1(DBrane_1_1 dBrane)
    {
        this.dBrane = dBrane;
    }

    DBraneWorldState analyze(DiplomacyGame diplomacyGame, CoalitionStructure cs)
    {
        allUnits.clear();
        Region aregion[];
        int k = (aregion = diplomacyGame.getAllRegions()).length;
        for(int j = 0; j < k; j++)
        {
            Region region = aregion[j];
            if(diplomacyGame.getController(region) != null)
                allUnits.add(region);
        }

        PowerSet involvedPowers[] = new PowerSet[34];
        List uncuttableUnits[][] = new List[34][128];
        dBrane.getClass();
        List conflictAreas = determineConflictAreas(diplomacyGame, cs, false);
        for(int p = 0; p < 7; p++)
        {
            Power power = DiplomacyGame.getPower(p);
            numCurrentlyOwned[p] = 0;
            for(Iterator iterator1 = power.getOwnedSCs().iterator(); iterator1.hasNext();)
            {
                Province sc = (Province)iterator1.next();
                if(conflictAreas.contains(sc))
                    numCurrentlyOwned[p]++;
            }

        }

        BattlePlan.ppsCreated = 0;
        BattlePlanStorage partialPlanStorage = BattlePlanStorage.getNew();
        for(Iterator iterator = DiplomacyGame.getSupplyCenters().iterator(); iterator.hasNext();)
        {
            Province sc = (Province)iterator.next();
            involvedPowers[sc.getId()] = determineInvolvedPowers(diplomacyGame, sc);
            Power owner = diplomacyGame.getOwner(sc);
            Power controller = diplomacyGame.getController(sc);
            Power protectedPower = null;
            boolean alliesDontAttack = false;
            dBrane.getClass();
            for(int subCoalitionId = 1; subCoalitionId < 128; subCoalitionId++)
            {
                PowerSet coalition = null;
                for(int i = 0; i < cs.getAllCoalitions().size(); i++)
                {
                    PowerSet coa = (PowerSet)cs.getAllCoalitions().get(i);
                    if(!LongSet.containsSubset(coa.getId(), subCoalitionId))
                        continue;
                    coalition = coa;
                    break;
                }

                if(coalition != null)
                {
                    PowerSet subCoalition = getPowerSetById(subCoalitionId);
                    dBrane.getClass();
                    PowerSet opponents = getComplementOfPowerSet(subCoalition);
                    if(alliesDontAttack && LongSet.getIntersection(subCoalitionId, cs.getAllies().getId()) != 0L && !LongSet.containsElement(subCoalitionId, protectedPower.getId()))
                    {
                        partialPlanStorage.addPlan(sc.getId(), new BattlePlan(subCoalitionId, sc));
                    } else
                    {
                        uncuttableUnits[sc.getId()][subCoalitionId] = new ArrayList();
                        RegionSet canSupport = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), subCoalition);
                        for(Iterator iterator2 = canSupport.iterator(); iterator2.hasNext();)
                        {
                            Region ourSupporter = (Region)iterator2.next();
                            RegionSet mayCut = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(ourSupporter.getProvince()), opponents);
                            RegionSet opponentSupporters = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), opponents);
                            boolean isUncuttable = true;
                            for(Iterator iterator4 = mayCut.iterator(); iterator4.hasNext();)
                            {
                                Region r = (Region)iterator4.next();
                                if(!opponentSupporters.contains(r) && r.getProvince().getId() != sc.getId())
                                {
                                    isUncuttable = false;
                                    break;
                                }
                            }

                            if(isUncuttable)
                                uncuttableUnits[sc.getId()][subCoalitionId].add(ourSupporter);
                        }

                        List partialPlans = determinePartialPlans(diplomacyGame, sc, subCoalition, opponents, uncuttableUnits);
                        discardAttacksWithUselessCuts(diplomacyGame, subCoalition, partialPlans);
                        Power controllerOwner = controller;
                        if(controller == null)
                            controllerOwner = owner;
                        dBrane.getClass();
                        if(!LongSet.containsSubset(cs.getAllies().getId(), subCoalitionId))
                            discardPlansWithPassivePowers(partialPlans);
                        dBrane.getClass();
                        markPlansWithUselessPowers(diplomacyGame, partialPlans, false);
                        partialPlanStorage.addPlans(sc.getId(), partialPlans);
                    }
                }
            }

            for(int subCoalitionId = 1; subCoalitionId < 128; subCoalitionId++)
            {
                PowerSet coalition = null;
                for(int i = 0; i < cs.getAllCoalitions().size(); i++)
                {
                    PowerSet coa = (PowerSet)cs.getAllCoalitions().get(i);
                    if(!LongSet.containsSubset(coa.getId(), subCoalitionId))
                        continue;
                    coalition = coa;
                    break;
                }

                if(coalition != null)
                {
                    PowerSet subCoalition = getPowerSetById(subCoalitionId);
                    dBrane.getClass();
                    PowerSet opponents = getComplementOfPowerSet(subCoalition);
                    List partialPlans = partialPlanStorage.getPartialPlansIncludingPassivePowers(sc.getId(), subCoalitionId);
                    ArrayList discardedPlans = getPlansWithTooMuchForce(diplomacyGame, partialPlanStorage, subCoalition, opponents, partialPlans);
                    if(discardedPlans != null)
                    {
                        BattlePlan discardedPlan;
                        for(Iterator iterator3 = discardedPlans.iterator(); iterator3.hasNext(); partialPlanStorage.remove(discardedPlan))
                            discardedPlan = (BattlePlan)iterator3.next();

                    }
                }
            }

            for(int subCoalitionId = 1; subCoalitionId < 128; subCoalitionId++)
            {
                PowerSet coalition = null;
                for(int i = 0; i < cs.getAllCoalitions().size(); i++)
                {
                    PowerSet coa = (PowerSet)cs.getAllCoalitions().get(i);
                    if(!LongSet.containsSubset(coa.getId(), subCoalitionId))
                        continue;
                    coalition = coa;
                    break;
                }

                if(coalition != null)
                {
                    PowerSet subCoalition = getPowerSetById(subCoalitionId);
                    dBrane.getClass();
                    PowerSet opponents = getComplementOfPowerSet(subCoalition);
                    RegionSet adjacentUnits = diplomacyGame.getAdjacentUnits(sc);
                    RegionSet opposingUnits = DipUtils.filterUnits(diplomacyGame, adjacentUnits, opponents);
                    int maxSupportForce = opposingUnits.size();
                    if(controller != null && opponents.contains(controller))
                        maxSupportForce++;
                    List partialPlans = partialPlanStorage.getPartialPlansIncludingPassivePowers(sc.getId(), subCoalitionId);
                    if(partialPlans != null)
                    {
                        for(int i = 0; i < partialPlans.size(); i++)
                        {
                            BattlePlan pp = (BattlePlan)partialPlans.get(i);
                            int uncuttableForce = pp.getUncuttableForce(diplomacyGame, opponents);
                            if((uncuttableForce > maxSupportForce || uncuttableForce == maxSupportForce && controller != null && coalition.contains(controller)) && pp.getSmallForce() > uncuttableForce)
                            {
                                partialPlanStorage.remove(pp);
                                i--;
                            }
                        }

                    }
                }
            }

        }

        return new DBraneWorldState(diplomacyGame, cs, conflictAreas, involvedPowers, partialPlanStorage);
    }

    static PlanListWithValue searchBestPlan(DBraneWorldState worldState, Power power, ArrayList agreements, boolean determineAuxiliaryPlans, long endTime)
    {
        BattlePlanStorage battlePlanStorage = BattlePlanStorage.getNew(worldState.getBattlePlanStorage());
        DiplomacyGame diplomacyGame = worldState.getDiplomacyGame();
        opposingPowers.clear();
        for(int i = 0; i < 7; i++)
            if(i != power.getId())
                opposingPowers.add(i);

        Arrays.fill(sc2agreement, null);
        Arrays.fill(sc2myAgreement, null);
        for(Iterator iterator = agreements.iterator(); iterator.hasNext();)
        {
            BattlePlan agreement = (BattlePlan)iterator.next();
            sc2agreement[agreement.getTargetProvince().getId()] = agreement;
            Power leader = BattlePlan.getLeadingPower(agreement, worldState.getDiplomacyGame());
            if(leader != null && leader.getId() == power.getId())
                sc2myAgreement[agreement.getTargetProvince().getId()] = agreement;
        }

        for(int i = 0; i < sc2foundSinglePlans.length; i++)
            if(sc2foundSinglePlans[i] != null)
                sc2foundSinglePlans[i].clear();

        for(int i = 0; i < sc2foundDoublePlans.length; i++)
        {
            for(int j = 0; j < sc2foundDoublePlans[i].length; j++)
                if(sc2foundDoublePlans[i][j] != null)
                    sc2foundDoublePlans[i][j].clear();

        }

        fixedOrders.clear();
        BattlePlan agreement;
        for(Iterator iterator1 = agreements.iterator(); iterator1.hasNext(); fixedOrders.addAll(agreement.allOrders))
            agreement = (BattlePlan)iterator1.next();

        removeIllegalPlans(battlePlanStorage, power, agreements);
        cleanPartialPlanStorage(battlePlanStorage, power, fixedOrders);
        removeIllegalHostilePlans(battlePlanStorage, power, agreements);
        cleanHostilePartialPlans(battlePlanStorage, power, fixedOrders);
        for(int i = 0; i < 34; i++)
        {
            if(defeatingPlanTable[i] != null)
                defeatingPlanTable[i].clear();
            List partialPlans = battlePlanStorage.getIndividualBattlePlans(i, power);
            if(partialPlans != null)
            {
                if(defeatingPlanTable[i] == null)
                    defeatingPlanTable[i] = new HashMap();
                for(Iterator iterator2 = partialPlans.iterator(); iterator2.hasNext();)
                {
                    BattlePlan pp = (BattlePlan)iterator2.next();
                    if(!pp.isEmpty())
                    {
                        List defeatingPlans = getDefeatingPlans(pp, diplomacyGame, battlePlanStorage, power, pp.getTargetProvince());
                        defeatingPlanTable[i].put(pp.toString(), defeatingPlans);
                        if(defeatingPlans.size() == 0)
                        {
                            if(sc2foundSinglePlans[i] == null)
                                sc2foundSinglePlans[i] = new ArrayList();
                            sc2foundSinglePlans[i].add(pp);
                        }
                    }
                }

            }
            if(diplomacyGame.getController(i) != null && diplomacyGame.getController(i).getId() == power.getId())
            {
                RegionSet adjacentUnits = diplomacyGame.getAdjacentUnits(DiplomacyGame.getProvince(i));
                adjacentUnits = DipUtils.filterUnits(diplomacyGame, adjacentUnits, opposingPowers);
                if(adjacentUnits.size() == 1)
                {
                    Province adjacentSC = adjacentUnits.get(0).getProvince();
                    if(adjacentSC.isSC())
                    {
                        partialPlans = battlePlanStorage.getIndividualBattlePlans(adjacentSC.getId(), power);
                        if(partialPlans != null)
                        {
                            for(Iterator iterator3 = partialPlans.iterator(); iterator3.hasNext();)
                            {
                                BattlePlan pp = (BattlePlan)iterator3.next();
                                if(pp.mainOrder != null && (pp.mainOrder instanceof MTOOrder) && pp.mainOrder.getLocation().getProvince().getId() == i && pp.allOrders.size() == 1)
                                {
                                    if(CompatibilityChecker.determineCompatibility(fixedOrders, pp.allOrders))
                                    {
                                        if(sc2foundSinglePlans[i] == null)
                                            sc2foundSinglePlans[i] = new ArrayList();
                                        sc2foundSinglePlans[i].add(pp);
                                    }
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }

        Arrays.fill(invincibleAgreements, false);
        for(int sc = 0; sc < 34; sc++)
        {
            BattlePlan agreement = sc2myAgreement[sc];
            if(agreement != null)
            {
                List defeatingPlans = getDefeatingPlans(agreement, diplomacyGame, battlePlanStorage, power, agreement.getTargetProvince());
                defeatingPlanTable[sc].put(agreement.toString(), defeatingPlans);
                if(defeatingPlans.size() == 0)
                    invincibleAgreements[sc] = true;
            }
        }

        boolean opponentsHaveConflictingUnits[][] = determineConflictingOpponentUnits(battlePlanStorage, power);
        fixedOrders2.clear();
        fixedOrders2.ensureCapacity(fixedOrders.size() * 2);
        for(int i = 0; i < 34; i++)
        {
            partialPlans0.clear();
            List pps = battlePlanStorage.getIndividualBattlePlans(i, power);
            if(pps != null)
                partialPlans0.addAll(pps);
            if(sc2myAgreement[i] != null && !invincibleAgreements[i])
                partialPlans0.add(sc2myAgreement[i]);
            if(partialPlans0.size() != 0)
            {
                for(int j = i + 1; j < 34; j++)
                    if(opponentsHaveConflictingUnits[i][j])
                    {
                        partialPlans1.clear();
                        pps = battlePlanStorage.getIndividualBattlePlans(j, power);
                        if(pps != null)
                            partialPlans1.addAll(pps);
                        if(sc2myAgreement[j] != null && !invincibleAgreements[i])
                            partialPlans1.add(sc2myAgreement[j]);
                        if(partialPlans1.size() != 0)
                        {
                            for(Iterator iterator4 = partialPlans0.iterator(); iterator4.hasNext();)
                            {
                                BattlePlan pp0 = (BattlePlan)iterator4.next();
                                List defPlans = (List)defeatingPlanTable[i].get(pp0.toString());
                                if(defPlans.size() != 0 && !pp0.isEmpty())
                                {
                                    fixedOrders2.clear();
                                    fixedOrders2.addAll(fixedOrders);
                                    fixedOrders2.addAll(pp0.allOrders);
                                    List defeatingPlans0 = (List)defeatingPlanTable[pp0.getTargetProvince().getId()].get(pp0.toString());
                                    for(Iterator iterator5 = partialPlans1.iterator(); iterator5.hasNext();)
                                    {
                                        BattlePlan pp1 = (BattlePlan)iterator5.next();
                                        defPlans = (List)defeatingPlanTable[j].get(pp1.toString());
                                        if(defPlans.size() != 0 && !pp1.isEmpty() && CompatibilityChecker.determineCompatibility(fixedOrders2, pp1.allOrders) && CompatibilityChecker.hasNoSelfCuts(fixedOrders2, pp1.allOrders))
                                        {
                                            List defeatingPlans1 = (List)defeatingPlanTable[pp1.getTargetProvince().getId()].get(pp1.toString());
                                            if(!legalCombinationExists(defeatingPlans0, defeatingPlans1))
                                            {
                                                if(sc2foundDoublePlans[i][j] == null)
                                                    sc2foundDoublePlans[i][j] = new ArrayList();
                                                sc2foundDoublePlans[i][j].add(new DoubleBattlePlan(pp0, pp1));
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }

            }
        }

        sc2chosenPlans = getBestCombination(sc2agreement, sc2foundSinglePlans, sc2foundDoublePlans, invincibleAgreements, endTime);
        if(sc2chosenPlans == null)
            return new PlanListWithValue();
        fixedOrders.clear();
        allFixedPlans.clear();
        Arrays.fill(noAuxplanNeeded, false);
        for(int sc = 0; sc < 34; sc++)
        {
            OrderContainer plan = sc2chosenPlans[sc];
            if(sc2agreement[sc] != null && plan == null)
                plan = sc2agreement[sc];
            else
            if(sc2agreement[sc] != null && sc2agreement[sc] != plan)
                throw new IllegalArgumentException((new StringBuilder("DBraneCore_1_0e.searchBestPlan() Error! plan returned by getBestCombination does not contain agreement \n agreement: ")).append(sc2agreement[sc]).append("\nreturned plan: ").append(plan).toString());
            if(plan != null)
            {
                noAuxplanNeeded[sc] = true;
                if(plan instanceof BattlePlan)
                    allFixedPlans.add((BattlePlan)plan);
                else
                if(plan instanceof DoubleBattlePlan)
                {
                    allFixedPlans.add(((DoubleBattlePlan)plan).pp0);
                    allFixedPlans.add(((DoubleBattlePlan)plan).pp1);
                    noAuxplanNeeded[((DoubleBattlePlan)plan).pp1.getTargetProvince().getId()] = true;
                } else
                {
                    throw new IllegalArgumentException((new StringBuilder("DBraneCore_1_0e.searchBestPlan() Error! unknown class: ")).append(plan.getClass().getName()).toString());
                }
                fixedOrders.addAll(plan.getAllOrders());
            }
        }

        int numEnsuredSCs = 0;
        long securedSCs = 0L;
        for(int sc = 0; sc < 34; sc++)
            if(sc2chosenPlans[sc] != null)
                if(sc2agreement[sc] != null)
                {
                    if(sc2myAgreement[sc] != null && invincibleAgreements[sc])
                    {
                        securedSCs = LongSet.addElement(securedSCs, sc);
                        numEnsuredSCs++;
                    }
                } else
                {
                    if(sc2chosenPlans[sc] instanceof BattlePlan)
                        securedSCs = LongSet.addElement(securedSCs, sc);
                    numEnsuredSCs++;
                }

        if(determineAuxiliaryPlans)
        {
            cleanPartialPlanStorage(battlePlanStorage, power, fixedOrders);
            Arrays.fill(sc2PartialPlans, null);
            for(int sc = 0; sc < 34; sc++)
                if(!noAuxplanNeeded[sc])
                {
                    List partialPlans = battlePlanStorage.getIndividualBattlePlans(sc, power);
                    if(partialPlans != null)
                    {
                        int size = partialPlans.size();
                        sc2PartialPlans[sc] = new ArrayList(size);
                        for(int j = size - 1; j >= 0; j--)
                            sc2PartialPlans[sc].add(partialPlans.get(j));

                        sc2PartialPlans[sc].add(new BattlePlan(1 << power.getId(), DiplomacyGame.getProvince(sc)));
                    }
                }

            FullPlanIterator fullPlanIterator = new FullPlanIterator(sc2PartialPlans);
            boolean consistentComboFound = false;
            BattlePlan fp[] = null;
            int counter = 0;
            while(!consistentComboFound && fullPlanIterator.hasNext()) 
            {
                counter++;
                fp = fullPlanIterator.next();
                fixedOrders2.clear();
                fixedOrders2.addAll(fixedOrders);
                for(int sc = 0; sc < 34; sc++)
                    if(fp[sc] != null)
                        fixedOrders2.addAll(fp[sc].allOrders);

                consistentComboFound = CompatibilityChecker.determineCompatibility(fixedOrders2);
                if(consistentComboFound)
                    consistentComboFound = CompatibilityChecker.hasNoSelfCuts(fixedOrders2);
            }
            if(!consistentComboFound && counter > 0)
                throw new IllegalArgumentException((new StringBuilder("DBraneCore_1_0.searchBestPlan() No consistent combination of auxiliary plans found!\nsc2PartialPlans:\n")).append(Arrays.toString(sc2PartialPlans)).append("\n\nAGREEMENTS: ").append(agreements).append("\n\nfixedOrders:\n").append(fixedOrders).toString());
            if(fp != null)
            {
                for(int sc = 0; sc < 34; sc++)
                    if(fp[sc] != null && !fp[sc].isEmpty())
                        allFixedPlans.add(fp[sc]);

            }
        }
        return new PlanListWithValue(allFixedPlans, numEnsuredSCs, securedSCs);
    }

    static boolean[][] determineConflictingOpponentUnits(BattlePlanStorage partialPlanStorage, Power power)
    {
        for(int i = 0; i < 34; i++)
            Arrays.fill(conflictingUnitsExist[i], false);

        for(int i = 0; i < 34; i++)
        {
            _units0.clear();
            BattlePlan pp;
            for(Iterator iterator = partialPlanStorage.getHostilePlans(i, power).iterator(); iterator.hasNext(); _units0.addAll(pp.units))
                pp = (BattlePlan)iterator.next();

            for(int j = i + 1; j < 34; j++)
            {
                _units1.clear();
                BattlePlan pp;
                for(Iterator iterator1 = partialPlanStorage.getHostilePlans(j, power).iterator(); iterator1.hasNext(); _units1.addAll(pp.units))
                    pp = (BattlePlan)iterator1.next();

                for(Iterator iterator2 = _units0.iterator(); iterator2.hasNext();)
                {
                    Region r = (Region)iterator2.next();
                    if(_units1.contains(r))
                    {
                        conflictingUnitsExist[i][j] = true;
                        break;
                    }
                }

            }

        }

        return conflictingUnitsExist;
    }

    public static boolean legalCombinationExists(List plans0, List plans1)
    {
        for(int i = 0; i < plans0.size(); i++)
        {
            BattlePlan defeatingPlan0 = (BattlePlan)plans0.get(i);
            for(int j = 0; j < plans1.size(); j++)
            {
                BattlePlan defeatingPlan1 = (BattlePlan)plans1.get(j);
                boolean compatible = CompatibilityChecker.determineCompatibility(defeatingPlan0.allOrders, defeatingPlan1.allOrders);
                if(compatible)
                    return true;
            }

        }

        return false;
    }

    static OrderContainer[] getBestCombination(BattlePlan _sc2Agreement[], List _sc2singlePlans[], List _sc2doublePlans[][], boolean _isInvincibleAgreement[], long endTime)
    {
        int sc = -1;
        int maxVarId = -1;
        Arrays.fill(varId2scID, -1);
        for(int varId = 0; varId < 34; varId++)
        {
            boolean foundOne = false;
            while(sc < 33) 
            {
                sc++;
                if(_sc2Agreement[sc] != null)
                {
                    foundOne = true;
                    break;
                }
                if(_sc2singlePlans[sc] != null && _sc2singlePlans[sc].size() != 0)
                {
                    foundOne = true;
                    break;
                }
                for(int i = 0; i < 34; i++)
                {
                    if(_sc2doublePlans[sc][i] == null || _sc2doublePlans[sc][i].size() == 0)
                        continue;
                    foundOne = true;
                    break;
                }

                if(foundOne)
                    break;
            }
            if(!foundOne)
                break;
            varId2scID[varId] = sc;
            maxVarId = varId;
        }

        DBraneValueCalculator dBraneValueCalculator = new DBraneValueCalculator(varId2scID, maxVarId + 1, _sc2Agreement, _isInvincibleAgreement);
        domains.clear();
        for(int varId = 0; varId <= maxVarId; varId++)
        {
            sc = varId2scID[varId];
            if(_sc2Agreement[sc] != null)
            {
                domains.add(varId, _sc2Agreement[sc]);
            } else
            {
                domains.add(varId, null);
                List plans = _sc2singlePlans[sc];
                if(plans != null)
                {
                    OrderContainer oc;
                    for(Iterator iterator = plans.iterator(); iterator.hasNext(); domains.add(varId, oc))
                        oc = (OrderContainer)iterator.next();

                }
                for(int i = 0; i < 34; i++)
                {
                    plans = _sc2doublePlans[sc][i];
                    if(sc >= i && plans != null)
                        throw new IllegalArgumentException("DBraneCore_1_0.getBestCombination() Error!");
                    if(plans != null && plans.size() != 0)
                    {
                        OrderContainer oc;
                        for(Iterator iterator1 = plans.iterator(); iterator1.hasNext(); domains.add(varId, oc))
                            oc = (OrderContainer)iterator1.next();

                    }
                }

            }
        }

        Graph graph = new Graph(maxVarId + 1);
        for(int i = 0; i <= maxVarId; i++)
        {
            units1.clear();
            List list1 = domains.get(i);
            for(Iterator iterator2 = list1.iterator(); iterator2.hasNext();)
            {
                OrderContainer oc = (OrderContainer)iterator2.next();
                if(oc != null)
                    units1.addAll(oc.getAllUnits());
            }

            for(int j = i + 1; j <= maxVarId; j++)
            {
                units2.clear();
                List list2 = domains.get(j);
                for(Iterator iterator3 = list2.iterator(); iterator3.hasNext();)
                {
                    OrderContainer oc = (OrderContainer)iterator3.next();
                    if(oc != null)
                        units2.addAll(oc.getAllUnits());
                }

                RegionSet intersection = intersect(units1, units2);
                if(intersection.size() > 0)
                    graph.setEdge(i, j);
            }

        }

        AndOrTree andOrTreeSearcher = new AndOrTree(domains, graph, dBraneValueCalculator);
        Object _solution[] = andOrTreeSearcher.expand(endTime - 200L);
        if(_solution == null)
            return null;
        OrderContainer solution[] = new OrderContainer[34];
        for(int i = 0; i < _solution.length; i++)
        {
            int scId = varId2scID[i];
            if(scId != -1)
                solution[scId] = (OrderContainer)_solution[i];
        }

        return solution;
    }

    static RegionSet intersect(RegionSet s1, RegionSet s2)
    {
        RegionSet intersection = new RegionSet();
        for(Iterator iterator = s1.iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            if(s2.contains(r))
                intersection.add(r);
        }

        return intersection;
    }

    static List getDefeatingPlans(BattlePlan pp, DiplomacyGame diplomacyGame, BattlePlanStorage partialPlanStorage, Power power, Province province)
    {
        Power controller = diplomacyGame.getController(province);
        Power owner = diplomacyGame.getOwner(province);
        List defeatingPlans = new ArrayList();
        for(Iterator iterator = partialPlanStorage.getHostilePlans(province.getId(), power).iterator(); iterator.hasNext();)
        {
            BattlePlan opponentPP = (BattlePlan)iterator.next();
            int idOfwinningCoalition = getWinningCoalition(pp, opponentPP, controller, owner);
            if(!LongSet.containsElement(idOfwinningCoalition, power.getId()) || idOfwinningCoalition == 0)
                defeatingPlans.add(opponentPP);
        }

        if(pp.mainOrder != null && (pp.mainOrder instanceof MTOOrder))
        {
            Province location = pp.mainOrder.getLocation().getProvince();
            if(location.isSC())
            {
                for(Iterator iterator1 = partialPlanStorage.getHostilePlans(province.getId(), power).iterator(); iterator1.hasNext();)
                {
                    BattlePlan opponentPP = (BattlePlan)iterator1.next();
                    if(opponentPP.mainOrder != null && (opponentPP.mainOrder instanceof MTOOrder) && opponentPP.mainOrder.getLocation().getProvince().getId() == pp.getTargetProvince().getId())
                    {
                        int idOfwinningCoalition = getWinningCoalition(pp, opponentPP, controller, owner);
                        if(!LongSet.containsElement(idOfwinningCoalition, power.getId()))
                            defeatingPlans.add(opponentPP);
                    }
                }

            }
        }
        return defeatingPlans;
    }

    public static int getWinningCoalition(BattlePlan ppA, BattlePlan ppB, Power currentController, Power currentOwner)
    {
        if(ppA.isEmpty && !ppB.isEmpty)
            return ppB.getCoalitionId();
        if(ppB.isEmpty)
            return ppA.getCoalitionId();
        if(!ppA.isEmpty || !ppB.isEmpty)
        {
            cutDestinations.clear();
            MTOOrder cutOrder;
            for(Iterator iterator = ppA.cuts.iterator(); iterator.hasNext(); cutDestinations.add(cutOrder.getDestination().getProvince()))
                cutOrder = (MTOOrder)iterator.next();

            MTOOrder cutOrder;
            for(Iterator iterator1 = ppB.cuts.iterator(); iterator1.hasNext(); cutDestinations.add(cutOrder.getDestination().getProvince()))
                cutOrder = (MTOOrder)iterator1.next();

            int strengthA = 1;
            for(Iterator iterator2 = ppA.supports.iterator(); iterator2.hasNext();)
            {
                Order supOrder = (Order)iterator2.next();
                if(!cutDestinations.contains(supOrder.getLocation().getProvince()))
                    strengthA++;
            }

            int strengthB = 1;
            for(Iterator iterator3 = ppB.supports.iterator(); iterator3.hasNext();)
            {
                Order supOrder = (Order)iterator3.next();
                if(!cutDestinations.contains(supOrder.getLocation().getProvince()))
                    strengthB++;
            }

            if(strengthA > strengthB)
                return ppA.getCoalitionId();
            if(strengthB > strengthA)
                return ppB.getCoalitionId();
        }
        if(currentController != null)
        {
            if(LongSet.containsElement(ppA.getCoalitionId(), currentController.getId()))
                return ppA.getCoalitionId();
            if(LongSet.containsElement(ppB.getCoalitionId(), currentController.getId()))
                return ppB.getCoalitionId();
        }
        if(currentOwner != null)
        {
            if(LongSet.containsElement(ppA.getCoalitionId(), currentOwner.getId()))
                return ppA.getCoalitionId();
            if(LongSet.containsElement(ppB.getCoalitionId(), currentOwner.getId()))
                return ppB.getCoalitionId();
        }
        return 0;
    }

    static ArrayList processPartialPlans(List partialPlans, Power me)
    {
        Arrays.fill(_orders1, null);
        for(Iterator iterator = partialPlans.iterator(); iterator.hasNext();)
        {
            BattlePlan pp = (BattlePlan)iterator.next();
            if(me == null || pp.isParticipating(me))
            {
                for(Iterator iterator1 = pp.allOrders.iterator(); iterator1.hasNext();)
                {
                    Order order = (Order)iterator1.next();
                    int provinceId = order.getLocation().getProvince().getId();
                    if(_orders1[provinceId] == null || DipUtils.areEqual(_orders1[provinceId], order))
                        _orders1[provinceId] = order;
                    else
                    if((order instanceof MTOOrder) || (_orders1[provinceId] instanceof MTOOrder))
                    {
                        if(!DipUtils.areEqual(_orders1[provinceId], order))
                        {
                            String s = (new StringBuilder("DBraneCore_1_0.processPartialPlans() Error! ")).append(order.toString()).append(" is not ").append(_orders1[provinceId].toString()).append(" ME: ").append(me).append(" partialPlans ").toString();
                            for(Iterator iterator2 = partialPlans.iterator(); iterator2.hasNext();)
                            {
                                BattlePlan ppp = (BattlePlan)iterator2.next();
                                s = (new StringBuilder(String.valueOf(s))).append(System.lineSeparator()).append(ppp.toString()).toString();
                            }

                            throw new IllegalArgumentException(s);
                        }
                    } else
                    if(_orders1[provinceId] instanceof HLDOrder)
                        _orders1[provinceId] = order;
                    else
                    if(!(order instanceof HLDOrder))
                    {
                        if((order instanceof SUPMTOOrder) || (_orders1[provinceId] instanceof SUPMTOOrder))
                            return null;
                        if((order instanceof SUPOrder) && (_orders1[provinceId] instanceof SUPOrder))
                        {
                            int supportedLocation1 = ((SUPOrder)order).getSupportedRegion().getId();
                            int supportedLocation2 = ((SUPOrder)_orders1[provinceId]).getSupportedRegion().getId();
                            if(supportedLocation1 != supportedLocation2)
                                return null;
                        } else
                        {
                            throw new IllegalArgumentException((new StringBuilder("DBrane_1_0.processPartialPlans() Error! ")).append(order).append(" vs. ").append(_orders1[provinceId]).toString());
                        }
                    }
                }

            }
        }

        for(int i = 0; i < 75; i++)
        {
            Order order = _orders1[i];
            if(order != null && (order instanceof SUPOrder))
            {
                Region supportedLocation = ((SUPOrder)order).getSupportedRegion();
                Order supportedOrder = _orders1[supportedLocation.getProvince().getId()];
                _orders1[i] = new SUPOrder(order.getPower(), order.getLocation(), supportedOrder);
            }
        }

        _orders.clear();
        Order aorder[];
        int k = (aorder = _orders1).length;
        for(int j = 0; j < k; j++)
        {
            Order order = aorder[j];
            if(order != null)
                _orders.add(order);
        }

        return _orders;
    }

    public static List determineConflictAreas(DiplomacyGame diplomacyGame, CoalitionStructure cs, boolean obeyImplicitAgreements)
    {
        ArrayList conflictAreas = new ArrayList(34);
        PowerSet us;
        if(obeyImplicitAgreements)
            us = cs.getAllies();
        else
            us = cs.getMeAsSet();
        PowerSet them = us.complement();
        for(Iterator iterator = DiplomacyGame.getSupplyCenters().iterator(); iterator.hasNext();)
        {
            Province sc = (Province)iterator.next();
            Power owner = diplomacyGame.getOwner(sc);
            Power controller = diplomacyGame.getController(sc);
            boolean weAreInvolved = false;
            if(controller != null && us.contains(controller))
            {
                weAreInvolved = true;
            } else
            {
                RegionSet ourUnitsNext = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), us);
                if(ourUnitsNext.size() != 0)
                    weAreInvolved = true;
            }
            boolean theyAreInvolved = false;
            if(controller != null && them.contains(controller))
            {
                theyAreInvolved = true;
            } else
            {
                RegionSet theirUnitsNext = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), them);
                if(theirUnitsNext.size() != 0)
                    theyAreInvolved = true;
            }
            boolean weOwnIt = owner != null && us.contains(owner);
            if(weAreInvolved && theyAreInvolved)
                conflictAreas.add(sc);
            else
            if(weAreInvolved && !weOwnIt)
                conflictAreas.add(sc);
            else
            if(theyAreInvolved && owner != null)
                them.contains(owner);
        }

        return conflictAreas;
    }

    static boolean isInvolvedInConflictArea(DiplomacyGame diplomacyGame, Province conflictArea, Power power, PowerSet opponents)
    {
        Power controller = diplomacyGame.getController(conflictArea);
        if(controller != null && controller.getId() == power.getId())
            return true;
        RegionSet myUnitsNext = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(conflictArea), power);
        if(myUnitsNext.size() > 0)
            return true;
        RegionSet opponentUnitsNext = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(conflictArea), opponents);
        for(Iterator iterator = opponentUnitsNext.iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            if(DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(r.getProvince()), power).size() > 0)
                return true;
        }

        return false;
    }

    public static List determinePartialPlans(DiplomacyGame diplomacyGame, Province sc, PowerSet subCoalition, PowerSet opposition, List uncuttableUnits[][])
    {
        int subCoalitionID = subCoalition.getId();
        Region controllingUnit = null;
        Power controllingPower = null;
        for(Iterator iterator = sc.getRegions().iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            controllingPower = diplomacyGame.getController(r);
            if(controllingPower != null)
            {
                controllingUnit = r;
                break;
            }
        }

        List partialPlans = new ArrayList();
        partialPlans.add(new BattlePlan(subCoalitionID, sc));
        RegionSet ourUnitsNext = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), subCoalition);
        RegionSet opponentsUnitsNext = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(sc), opposition);
        supAndCutOrders.clear();
        if(controllingUnit != null && subCoalition.contains(controllingPower))
        {
            HLDOrder hold = new HLDOrder(controllingPower, controllingUnit);
            Region unit;
            for(Iterator iterator1 = ourUnitsNext.iterator(); iterator1.hasNext(); supAndCutOrders.add(new SUPOrder(diplomacyGame.getController(unit), unit, hold)))
                unit = (Region)iterator1.next();

            if(opponentsUnitsNext.size() > 1)
            {
                for(Iterator iterator2 = opponentsUnitsNext.iterator(); iterator2.hasNext();)
                {
                    Region oppUnit = (Region)iterator2.next();
                    for(Iterator iterator4 = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(oppUnit.getProvince()), subCoalition).iterator(); iterator4.hasNext();)
                    {
                        Region cuttingUnit = (Region)iterator4.next();
                        if(cuttingUnit.getId() != controllingUnit.getId() && !uncuttableUnits[sc.getId()][subCoalitionID].contains(cuttingUnit))
                        {
                            Region destination = null;
                            for(Iterator iterator7 = oppUnit.getProvince().getRegions().iterator(); iterator7.hasNext();)
                            {
                                Region r = (Region)iterator7.next();
                                if(DiplomacyGame.getDistance(cuttingUnit, r) == 1)
                                    destination = r;
                            }

                            if(destination == null)
                                throw new IllegalArgumentException((new StringBuilder("SomeClass.getPlansOf() Error! It seems that region ")).append(oppUnit.getName()).append(" is not next to ").append(cuttingUnit.getName()).toString());
                            Power power = diplomacyGame.getController(cuttingUnit);
                            supAndCutOrders.add(new MTOOrder(power, cuttingUnit, destination));
                        }
                    }

                }

            }
            int numSubSets = 1 << supAndCutOrders.size();
            for(int i = 0; i < numSubSets; i++)
            {
                ArrayList orders = getSubset(supAndCutOrders, i);
                if(areConsistent(orders))
                    partialPlans.add(new BattlePlan(subCoalitionID, hold, orders));
            }

        }
        for(Iterator iterator3 = ourUnitsNext.iterator(); iterator3.hasNext();)
        {
            Region unit = (Region)iterator3.next();
            Region destination = getDestination(unit, sc);
            MTOOrder move = new MTOOrder(diplomacyGame.getController(unit), unit, destination);
            supAndCutOrders.clear();
            for(Iterator iterator5 = ourUnitsNext.iterator(); iterator5.hasNext();)
            {
                Region supportingUnit = (Region)iterator5.next();
                if(supportingUnit.getId() != unit.getId())
                    supAndCutOrders.add(new SUPMTOOrder(diplomacyGame.getController(supportingUnit), supportingUnit, move));
            }

            if(opponentsUnitsNext.size() > 1 || opponentsUnitsNext.size() == 1 && controllingPower != null && opposition.contains(controllingPower))
            {
                for(Iterator iterator6 = opponentsUnitsNext.iterator(); iterator6.hasNext();)
                {
                    Region oppUnit = (Region)iterator6.next();
                    for(Iterator iterator8 = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(oppUnit.getProvince()), subCoalition).iterator(); iterator8.hasNext();)
                    {
                        Region cuttingUnit = (Region)iterator8.next();
                        if(cuttingUnit.getId() != unit.getId() && cuttingUnit.getProvince().getId() != sc.getId() && !uncuttableUnits[sc.getId()][subCoalitionID].contains(cuttingUnit))
                        {
                            Region dest = null;
                            for(Iterator iterator9 = oppUnit.getProvince().getRegions().iterator(); iterator9.hasNext();)
                            {
                                Region r = (Region)iterator9.next();
                                if(DiplomacyGame.getDistance(cuttingUnit, r) == 1)
                                    dest = r;
                            }

                            if(dest == null)
                                throw new IllegalArgumentException((new StringBuilder("SomeClass.getPlansOf() Error! It seems that region ")).append(oppUnit.getName()).append(" is not next to ").append(cuttingUnit.getName()).toString());
                            Power power = diplomacyGame.getController(cuttingUnit);
                            supAndCutOrders.add(new MTOOrder(power, cuttingUnit, dest));
                        }
                    }

                }

            }
            int numSubSets = 1 << supAndCutOrders.size();
            for(int i = 0; i < numSubSets; i++)
            {
                List orders = getSubset(supAndCutOrders, i);
                if(areConsistent(orders))
                    partialPlans.add(new BattlePlan(subCoalitionID, move, orders));
            }

        }

        Power owner = diplomacyGame.getOwner(sc);
        if(controllingUnit == null && owner != null && subCoalition.contains(owner) && ourUnitsNext.size() >= 2)
        {
            List allPossibleMoves = new ArrayList();
            for(int i = 0; i < ourUnitsNext.size(); i++)
            {
                Region unit = ourUnitsNext.get(i);
                Region destination = getDestination(unit, sc);
                Power power = diplomacyGame.getController(unit);
                allPossibleMoves.add(new MTOOrder(power, unit, destination));
            }

            for(int i = 0; i < allPossibleMoves.size(); i++)
            {
                for(int j = i + 1; j < allPossibleMoves.size(); j++)
                {
                    BattlePlan bounce = new BattlePlan(subCoalitionID, (MTOOrder)allPossibleMoves.get(i), (MTOOrder)allPossibleMoves.get(j));
                    partialPlans.add(bounce);
                }

            }

        }
        return partialPlans;
    }

    public static PowerSet determineInvolvedPowers(DiplomacyGame diplomacyGame, Province conflictArea)
    {
        PowerSet involvedPowers = new PowerSet();
        Power controller = diplomacyGame.getController(conflictArea);
        if(controller != null)
            involvedPowers.add(controller);
        RegionSet adjacentUnits = diplomacyGame.getAdjacentUnits(conflictArea);
        Power adjacentPower;
        for(Iterator iterator = adjacentUnits.iterator(); iterator.hasNext(); involvedPowers.add(adjacentPower))
        {
            Region adjacentUnit = (Region)iterator.next();
            adjacentPower = diplomacyGame.getController(adjacentUnit);
        }

        if(involvedPowers.size() == 1)
            return involvedPowers;
        for(Iterator iterator1 = adjacentUnits.iterator(); iterator1.hasNext();)
        {
            Region adjacentUnit = (Region)iterator1.next();
            Power adjacentPower = diplomacyGame.getController(adjacentUnit);
            RegionSet possibleCuttingUnits = diplomacyGame.getAdjacentUnits(adjacentUnit.getProvince());
            for(Iterator iterator2 = possibleCuttingUnits.iterator(); iterator2.hasNext();)
            {
                Region possibleCuttingUnit = (Region)iterator2.next();
                Power possibleCuttingPower = diplomacyGame.getController(possibleCuttingUnit);
                if(possibleCuttingPower.getId() != adjacentPower.getId())
                    involvedPowers.add(possibleCuttingPower);
            }

        }

        return involvedPowers;
    }

    public static void cleanPartialPlanStorage(BattlePlanStorage partialPlanStorage, Power power, List fixedOrders)
    {
        discarded.clear();
        Arrays.fill(destination2location, -1);
        for(Iterator iterator = fixedOrders.iterator(); iterator.hasNext();)
        {
            Order o = (Order)iterator.next();
            if(o instanceof MTOOrder)
            {
                MTOOrder move = (MTOOrder)o;
                destination2location[move.getDestination().getProvince().getId()] = move.getLocation().getProvince().getId();
            }
        }

        for(int sc = 0; sc < 34; sc++)
        {
            List partialPlans = partialPlanStorage.getIndividualBattlePlans(sc, power);
            if(partialPlans != null)
            {
                for(int i = 0; i < partialPlans.size(); i++)
                {
                    BattlePlan pp = (BattlePlan)partialPlans.get(i);
                    if(!CompatibilityChecker.determineCompatibility(fixedOrders, pp.allOrders) || !CompatibilityChecker.hasNoSelfCuts(fixedOrders, pp.allOrders))
                    {
                        discarded.add(pp);
                    } else
                    {
                        Iterator iterator1 = pp.allOrders.iterator();
                        while(iterator1.hasNext()) 
                        {
                            Order o = (Order)iterator1.next();
                            if(!(o instanceof MTOOrder))
                                continue;
                            MTOOrder move = (MTOOrder)o;
                            if(destination2location[move.getDestination().getProvince().getId()] == -1 || destination2location[move.getDestination().getProvince().getId()] == move.getLocation().getProvince().getId())
                                continue;
                            discarded.add(pp);
                            break;
                        }
                    }
                }

            }
        }

        for(int i = 0; i < discarded.size(); i++)
        {
            BattlePlan pp = (BattlePlan)discarded.get(i);
            partialPlanStorage.remove(pp);
        }

    }

    public static void cleanHostilePartialPlans(BattlePlanStorage partialPlanStorage, Power power, List fixedOrders)
    {
        for(int sc = 0; sc < 34; sc++)
        {
            List partialPlans = partialPlanStorage.getHostilePlans(sc, power);
            if(partialPlans != null)
            {
                for(int i = 0; i < partialPlans.size(); i++)
                {
                    BattlePlan pp = (BattlePlan)partialPlans.get(i);
                    if(!CompatibilityChecker.determineCompatibility(fixedOrders, pp.allOrders))
                        discarded.add(pp);
                }

            }
        }

        for(int i = 0; i < discarded.size(); i++)
        {
            BattlePlan pp = (BattlePlan)discarded.get(i);
            partialPlanStorage.remove(pp);
        }

    }

    private static void removeIllegalPlans(BattlePlanStorage partialPlanStorage, Power power, List agreements)
    {
        discarded.clear();
        for(int i = 0; i < agreements.size(); i++)
        {
            BattlePlan agreement = (BattlePlan)agreements.get(i);
            if(LongSet.containsElement(agreement.getCoalitionId(), power.getId()))
            {
                int scId = agreement.getTargetProvince().getId();
                List plansOnSC = partialPlanStorage.getIndividualBattlePlans(scId, power);
                if(plansOnSC != null)
                {
                    for(int j = 0; j < plansOnSC.size(); j++)
                    {
                        BattlePlan pp2 = (BattlePlan)plansOnSC.get(j);
                        if(agreement != pp2)
                            discarded.add(pp2);
                    }

                }
            }
        }

        for(int i = 0; i < discarded.size(); i++)
        {
            BattlePlan pp = (BattlePlan)discarded.get(i);
            partialPlanStorage.remove(pp);
        }

    }

    private static void removeIllegalHostilePlans(BattlePlanStorage partialPlanStorage, Power power, List agreements)
    {
        discarded.clear();
        for(int i = 0; i < agreements.size(); i++)
        {
            BattlePlan agreement = (BattlePlan)agreements.get(i);
            int scId = agreement.getTargetProvince().getId();
            List plansOnSC = partialPlanStorage.getHostilePlans(scId, power);
            for(int j = 0; j < plansOnSC.size(); j++)
            {
                BattlePlan pp2 = (BattlePlan)plansOnSC.get(j);
                if(LongSet.getIntersection(agreement.getCoalitionId(), pp2.getCoalitionId()) != 0L && agreement != pp2)
                    discarded.add(pp2);
            }

        }

        for(int i = 0; i < discarded.size(); i++)
        {
            BattlePlan pp = (BattlePlan)discarded.get(i);
            partialPlanStorage.remove(pp);
        }

    }

    public static void discardAttacksWithUselessCuts(DiplomacyGame diplomacyGame, PowerSet subCoalition, List partialPlans)
    {
        int size = partialPlans.size();
        cleanList.clear();
        cleanList.ensureCapacity(size);
        for(int i = 0; i < size; i++)
        {
            BattlePlan partialPlan = (BattlePlan)partialPlans.get(i);
            if(partialPlan.isEmpty())
                cleanList.add(partialPlan);
            else
            if(isDefensive(diplomacyGame, partialPlan, subCoalition))
            {
                cleanList.add(partialPlan);
            } else
            {
                Power owner = diplomacyGame.getOwner(partialPlan.getTargetProvince());
                Power controller = diplomacyGame.getController(partialPlan.getTargetProvince());
                if(owner == null && (controller == null || subCoalition.contains(controller)))
                    cleanList.add(partialPlan);
                else
                if(partialPlan.supports.size() != 0 || partialPlan.cuts.size() <= 0)
                    cleanList.add(partialPlan);
            }
        }

        partialPlans.clear();
        partialPlans.addAll(cleanList);
    }

    void discardPlansWithPassivePowers(List partialPlans)
    {
        int size = partialPlans.size();
        cleanList.clear();
        cleanList.ensureCapacity(size);
        for(int i = 0; i < size; i++)
        {
            BattlePlan pp = (BattlePlan)partialPlans.get(i);
            if(pp.getPassivePowers().size() == 0)
                cleanList.add(pp);
        }

        partialPlans.clear();
        partialPlans.addAll(cleanList);
    }

    public static void markPlansWithUselessPowers(DiplomacyGame diplomacyGame, List partialPlans, boolean obeyImplicitAgreements)
    {
        for(Iterator iterator = partialPlans.iterator(); iterator.hasNext();)
        {
            BattlePlan partialPlan = (BattlePlan)iterator.next();
            boolean isOkay = true;
            Iterator iterator1 = partialPlan.getPassivePowers().iterator();
            while(iterator1.hasNext()) 
            {
                int powerId = ((Integer)iterator1.next()).intValue();
                Power nonActivePower = DiplomacyGame.getPower(powerId);
                if(diplomacyGame.getController(partialPlan.getTargetProvince()) != null && diplomacyGame.getController(partialPlan.getTargetProvince()).getId() == powerId)
                    continue;
                RegionSet canSupport = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(partialPlan.getTargetProvince()), nonActivePower);
                if(canSupport.size() > 0)
                    continue;
                if(obeyImplicitAgreements && partialPlan.supports != null)
                {
                    boolean isOkay2 = false;
                    for(Iterator iterator2 = partialPlan.supports.iterator(); iterator2.hasNext();)
                    {
                        Order support = (Order)iterator2.next();
                        RegionSet mayCut = DipUtils.filterUnits(diplomacyGame, diplomacyGame.getAdjacentUnits(support.getLocation().getProvince()), nonActivePower);
                        if(mayCut.size() > 0)
                        {
                            isOkay2 = true;
                            break;
                        }
                    }

                    if(isOkay2)
                        continue;
                }
                isOkay = false;
                break;
            }
            if(!isOkay)
                partialPlan.hasUselessPowers = true;
        }

    }

    public static ArrayList getPlansWithTooMuchForce(DiplomacyGame diplomacyGame, BattlePlanStorage partialPlanStorage, PowerSet subCoalition, PowerSet opponents, List partialPlans)
    {
        if(partialPlans == null)
            return null;
        ArrayList discardedPlans = new ArrayList();
        for(Iterator iterator = partialPlans.iterator(); iterator.hasNext();)
        {
            BattlePlan partialPlan = (BattlePlan)iterator.next();
            if(!partialPlan.isEmpty())
            {
                boolean defensivePlan = isDefensive(diplomacyGame, partialPlan, subCoalition);
                Power targetController = diplomacyGame.getController(partialPlan.getTargetProvince());
                boolean targetIsOccupied = targetController != null;
                if(ForceCalculator.isTooStrong(diplomacyGame, partialPlan, partialPlanStorage, subCoalition, opponents, !defensivePlan, targetIsOccupied))
                    discardedPlans.add(partialPlan);
            }
        }

        return discardedPlans;
    }

    public static boolean isDefensive(DiplomacyGame diplomacyGame, BattlePlan partialPlan, PowerSet coalition)
    {
        Province sc = partialPlan.getTargetProvince();
        boolean defensivePlan = false;
        if(diplomacyGame.getController(sc) != null && coalition.contains(diplomacyGame.getController(sc)))
            defensivePlan = true;
        else
        if(diplomacyGame.getController(sc) == null && diplomacyGame.getOwner(sc) != null && coalition.contains(diplomacyGame.getOwner(sc)))
            defensivePlan = true;
        return defensivePlan;
    }

    static ArrayList getSubset(List set, int subsetIndex)
    {
        int maxIndex = (1 << set.size()) - 1;
        if(subsetIndex < 0 || subsetIndex > maxIndex)
            throw new IllegalArgumentException((new StringBuilder("getSubset() Error! subsetIndex is: ")).append(subsetIndex).append(", but must be between 0 and ").append(maxIndex).toString());
        ArrayList subset = new ArrayList(set.size());
        for(int j = 0; j < set.size(); j++)
            if((1 << j & subsetIndex) == 1 << j)
                subset.add((Order)set.get(j));

        return subset;
    }

    static boolean areConsistent(List orders)
    {
        for(int j = 0; j < orders.size(); j++)
        {
            for(int k = j + 1; k < orders.size(); k++)
            {
                Order order1 = (Order)orders.get(j);
                Order order2 = (Order)orders.get(k);
                if(order1.getLocation().getId() == order2.getLocation().getId())
                    return false;
                if((order1 instanceof MTOOrder) && (order2 instanceof MTOOrder) && ((MTOOrder)order1).getDestination().getProvince().getId() == ((MTOOrder)order2).getDestination().getProvince().getId())
                    return false;
            }

        }

        return true;
    }

    static Region getDestination(Region unit, Province sc)
    {
        Region destination = null;
        for(Iterator iterator = DiplomacyGame.getNeighbors(unit).iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            if(r.getProvince().getId() == sc.getId())
            {
                destination = r;
                break;
            }
        }

        if(destination == null)
            throw new IllegalArgumentException((new StringBuilder("SomeClass.getDestination() Error! The unit to attack sc seems not to be next to the sc. Unit: ")).append(unit.getName()).append(" SC: ").append(sc.getName()).append("\n").append(" The neighbors of ").append(unit.getName()).append(" are: ").append(DiplomacyGame.getNeighbors(unit).toString()).toString());
        else
            return destination;
    }

    static PowerSet getComplementOfPowerSet(PowerSet ps)
    {
        int fullSetId = 127;
        int complementId = fullSetId - ps.getId();
        return getPowerSetById(complementId);
    }

    static PowerSet getPowerSetById(int id)
    {
        if(id2powerSet[id] == null)
            id2powerSet[id] = new PowerSet(id);
        return id2powerSet[id];
    }

    static List allUnits = new ArrayList(34);
    static PowerSet unit2Coalition[] = new PowerSet[120];
    static int numCurrentlyOwned[] = new int[7];
    static PowerSet id2powerSet[] = new PowerSet[128];
    static Random random = new Random();
    DBrane_1_1 dBrane;
    static PowerSet opposingPowers = new PowerSet();
    static BattlePlan sc2agreement[] = new BattlePlan[34];
    static BattlePlan sc2myAgreement[] = new BattlePlan[34];
    static List sc2foundSinglePlans[] = new List[34];
    static List sc2foundDoublePlans[][] = new List[34][34];
    static HashMap defeatingPlanTable[] = new HashMap[34];
    static boolean invincibleAgreements[] = new boolean[34];
    static List fixedOrders = new ArrayList();
    static ArrayList fixedOrders2 = new ArrayList();
    static OrderContainer sc2chosenPlans[];
    static ArrayList allFixedPlans = new ArrayList();
    static boolean noAuxplanNeeded[] = new boolean[34];
    static List partialPlans0 = new ArrayList();
    static List partialPlans1 = new ArrayList();
    static List sc2PartialPlans[] = new List[34];
    static boolean conflictingUnitsExist[][] = new boolean[34][34];
    static RegionSet _units0 = new RegionSet();
    static RegionSet _units1 = new RegionSet();
    static int varId2scID[] = new int[35];
    static RegionSet units1 = new RegionSet();
    static RegionSet units2 = new RegionSet();
    static ArrayOfLists domains = new ArrayOfLists(34, 10);
    static ProvinceSet cutDestinations = new ProvinceSet();
    static ArrayList _orders = new ArrayList(34);
    static Order _orders1[] = new Order[75];
    static List supAndCutOrders = new ArrayList();
    static List discarded = new ArrayList();
    static int destination2location[] = new int[75];
    static ArrayList cleanList = new ArrayList();

}
