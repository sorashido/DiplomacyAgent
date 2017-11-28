// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DBraneWorldState.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.DiplomacyGame;
import ddejonge.dipgameExtensions.PowerSet;
import ddejonge.nb3.domain.Nb3WorldState;
import es.csic.iiia.fabregues.dip.board.Province;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            CoalitionStructure, BattlePlanStorage, DBraneAction, BattlePlan, 
//            CompatibilityChecker

public class DBraneWorldState extends Nb3WorldState
{

    public DBraneWorldState(DiplomacyGame diplomacyGame, CoalitionStructure coalitionStructure, List conflictAreas, PowerSet sc2involvedPowers[], BattlePlanStorage partialPlanStorage)
    {
        confirmedActions = new ArrayList();
        allOrders = new ArrayList();
        if(sc2involvedPowers == null)
        {
            throw new IllegalArgumentException("DBraneWorldState.DBraneWorldState() Error! sc2involvedPowers == null");
        } else
        {
            this.diplomacyGame = diplomacyGame;
            this.coalitionStructure = coalitionStructure;
            this.conflictAreas = conflictAreas;
            this.sc2involvedPowers = sc2involvedPowers;
            this.partialPlanStorage = partialPlanStorage;
            return;
        }
    }

    public boolean isLegal(ArrayList actions)
    {
        ArrayList dBraneActions = actions;
        allOrders.clear();
        BattlePlan pp;
        for(Iterator iterator = dBraneActions.iterator(); iterator.hasNext(); allOrders.addAll(pp.allOrders))
        {
            DBraneAction dBraneAction = (DBraneAction)iterator.next();
            pp = dBraneAction.partialPlan;
            if(pp == null)
                return false;
        }

        DBraneAction dBraneAction;
        for(Iterator iterator1 = confirmedActions.iterator(); iterator1.hasNext(); allOrders.addAll(dBraneAction.partialPlan.allOrders))
            dBraneAction = (DBraneAction)iterator1.next();

        return CompatibilityChecker.determineCompatibility(allOrders);
    }

    public void update(ArrayList actions)
    {
        confirmedActions.addAll(actions);
    }

    public Nb3WorldState copy()
    {
        return new DBraneWorldState(diplomacyGame, coalitionStructure, conflictAreas, sc2involvedPowers, partialPlanStorage);
    }

    public List getConflictAreas()
    {
        return conflictAreas;
    }

    DiplomacyGame getDiplomacyGame()
    {
        return diplomacyGame;
    }

    public CoalitionStructure getCoalitionStructure()
    {
        return coalitionStructure;
    }

    public BattlePlanStorage getBattlePlanStorage()
    {
        return partialPlanStorage;
    }

    public PowerSet getInvolvedPowers(Province sc)
    {
        return sc2involvedPowers[sc.getId()];
    }

    private DiplomacyGame diplomacyGame;
    private CoalitionStructure coalitionStructure;
    private PowerSet sc2involvedPowers[];
    private List conflictAreas;
    private BattlePlanStorage partialPlanStorage;
    ArrayList confirmedActions;
    List allOrders;
}
