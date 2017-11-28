// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CoalitionStructure.java

package ddejonge.dBrane_1_1;

import ddejonge.dipgameExtensions.DiplomacyGame;
import ddejonge.dipgameExtensions.PowerSet;
import ddejonge.nb3.tools.LongSet;
import es.csic.iiia.fabregues.dip.board.Power;
import java.io.PrintStream;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            DBraneAction, BattlePlan

class CoalitionStructure
{

    CoalitionStructure()
    {
        creditsReceived = new int[7][7];
        meAsSet = new PowerSet();
        allCoalitions = new ArrayList(7);
        negotiatingPowers = new PowerSet();
    }

    CoalitionStructure(String string, Power me)
    {
        creditsReceived = new int[7][7];
        meAsSet = new PowerSet();
        allCoalitions = new ArrayList(7);
        negotiatingPowers = new PowerSet();
        this.me = me;
        meAsSet.add(me);
        string = string.replace(" ", "");
        string = string.replace("{", "");
        String coalitionStrings[] = string.split("}");
        negotiatingPowers = new PowerSet();
        String as[];
        int j = (as = coalitionStrings).length;
        for(int i = 0; i < j; i++)
        {
            String coalitionString = as[i];
            boolean negotiating = false;
            if(coalitionString.startsWith("n"))
            {
                negotiating = true;
                coalitionString = coalitionString.replace("n", "");
            }
            PowerSet coalition = new PowerSet();
            String powerNames[] = coalitionString.split(",");
            String as1[];
            int l = (as1 = powerNames).length;
            for(int k = 0; k < l; k++)
            {
                String powerName = as1[k];
                Power power = DiplomacyGame.getPower(powerName);
                if(power == null)
                    System.out.println((new StringBuilder("CoalitionStructure.CoalitionStructure() Power with name ")).append(powerName).append(" Is null!!!").toString());
                coalition.add(power);
            }

            if(negotiating)
                negotiatingPowers.add(coalition);
        }

        allies = new PowerSet();
        if(negotiatingPowers.contains(me))
        {
            allies.add(negotiatingPowers);
            allCoalitions.add(allies);
            PowerSet ps;
            for(Iterator iterator = negotiatingPowers.complement().iterator(); iterator.hasNext(); allCoalitions.add(ps))
            {
                int opp = ((Integer)iterator.next()).intValue();
                ps = new PowerSet();
                ps.add(opp);
            }

        } else
        {
            allies = negotiatingPowers.complement();
            allCoalitions.add(allies);
            allCoalitions.add(new PowerSet(negotiatingPowers));
        }
    }

    CoalitionStructure(Power me)
    {
        creditsReceived = new int[7][7];
        meAsSet = new PowerSet();
        allCoalitions = new ArrayList(7);
        negotiatingPowers = new PowerSet();
        this.me = me;
        meAsSet.add(me);
        allies = new PowerSet();
        allies.add(me);
        allCoalitions.add(allies);
        for(int i = 0; i < 7; i++)
            if(i != me.getId())
            {
                Power opponent = DiplomacyGame.getPower(i);
                PowerSet ps = new PowerSet();
                ps.add(opponent);
                allCoalitions.add(ps);
            }

    }

    CoalitionStructure(PowerSet coalition, Power me, boolean theCoalitionNegotiates)
    {
        creditsReceived = new int[7][7];
        meAsSet = new PowerSet();
        allCoalitions = new ArrayList(7);
        negotiatingPowers = new PowerSet();
        this.me = me;
        meAsSet.add(me);
        allies = new PowerSet();
        negotiatingPowers = new PowerSet();
        if(theCoalitionNegotiates)
            negotiatingPowers.add(coalition);
        if(coalition.contains(me))
        {
            allies.add(coalition);
            allCoalitions.add(coalition);
            PowerSet ps;
            for(Iterator iterator = coalition.complement().iterator(); iterator.hasNext(); allCoalitions.add(ps))
            {
                int opp = ((Integer)iterator.next()).intValue();
                ps = new PowerSet();
                ps.add(opp);
            }

        } else
        {
            allies = coalition.complement();
            allCoalitions.add(allies);
            allCoalitions.add(new PowerSet(coalition));
        }
    }

    Power getMe()
    {
        return me;
    }

    PowerSet getMeAsSet()
    {
        return meAsSet;
    }

    PowerSet getAllies()
    {
        return allies;
    }

    ArrayList getAllCoalitions()
    {
        return allCoalitions;
    }

    PowerSet getNegotiatingPowers()
    {
        return negotiatingPowers;
    }

    void addCredits(Power donor, Power acquirer, int numCredits)
    {
        addCredits(donor.getId(), acquirer.getId(), numCredits);
    }

    void addCredits(int donorId, int acquirerId, int numCredits)
    {
        creditsReceived[donorId][acquirerId] += numCredits;
        creditsReceived[acquirerId][donorId] -= numCredits;
    }

    int getCredits(int donorId, int acquirerId)
    {
        return creditsReceived[donorId][acquirerId];
    }

    float getCreditUtility(Power power)
    {
        return getCreditUtility(power.getId());
    }

    float getCreditUtility(int powerId)
    {
        return getCreditUtility(powerId, creditsReceived, null);
    }

    static float getCreditUtility(int powerId, int credits[][], PowerSet allies)
    {
        float totalUtility = 0.0F;
        for(int donorId = 0; donorId < 7; donorId++)
            if(credits[donorId][powerId] > 0)
                totalUtility += 0.4F * (float)credits[donorId][powerId];
            else
                totalUtility += 0.55F * (float)credits[donorId][powerId];

        return totalUtility;
    }

    void updateWithActions(List actions, DiplomacyGame diplomacyGame)
    {
        DBraneAction action;
        for(Iterator iterator = actions.iterator(); iterator.hasNext(); update(action.getPartialPlan(), diplomacyGame))
            action = (DBraneAction)iterator.next();

    }

    void updateWithPartialPlans(List partialPlans, DiplomacyGame diplomacyGame)
    {
        BattlePlan partialPlan;
        for(Iterator iterator = partialPlans.iterator(); iterator.hasNext(); update(partialPlan, diplomacyGame))
            partialPlan = (BattlePlan)iterator.next();

    }

    void update(BattlePlan partialPlan, DiplomacyGame diplomacyGame)
    {
        int coaltionPartners = partialPlan.getCoalitionId();
        Power leadingPower = BattlePlan.getLeadingPower(partialPlan, diplomacyGame);
        for(int powerId = 0; powerId < 7; powerId++)
            if(LongSet.containsElement(coaltionPartners, powerId) && leadingPower != null && powerId != leadingPower.getId())
                addCredits(leadingPower.getId(), powerId, 1);

    }

    void copy(CoalitionStructure original)
    {
        for(int i = 0; i < creditsReceived.length; i++)
        {
            for(int j = 0; j < creditsReceived.length; j++)
                creditsReceived[i][j] = original.creditsReceived[i][j];

        }

        me = original.me;
        meAsSet.makeEqualTo(original.meAsSet);
        allies = original.allies;
        negotiatingPowers = original.negotiatingPowers;
    }

    public static final float OUTSTANDING_CREDIT_VALUE = 0.55F;
    public static final float CREDIT_VALUE = 0.4F;
    private int creditsReceived[][];
    private Power me;
    private PowerSet meAsSet;
    private PowerSet allies;
    private ArrayList allCoalitions;
    PowerSet negotiatingPowers;
}
