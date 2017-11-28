// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DBrane_1_1.java

package ddejonge.dBrane_1_1;

import ddejonge.dBrane.tools.ArrayOfLists;
import ddejonge.dBrane.tools.Utils;
import ddejonge.dipgameExtensions.*;
import es.csic.iiia.fabregues.dip.Player;
import es.csic.iiia.fabregues.dip.board.*;
import es.csic.iiia.fabregues.dip.comm.Comm;
import es.csic.iiia.fabregues.dip.comm.CommException;
import es.csic.iiia.fabregues.dip.comm.daide.DaideComm;
import es.csic.iiia.fabregues.dip.orders.*;
import es.csic.iiia.fabregues.utilities.Interface;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            DBraneCore_1_1, DiplomacyInstanceInfo, CoalitionStructure, DBraneWorldState, 
//            PlanListWithValue, ExtraOrderFinder, DBraneNegotiator

public class DBrane_1_1 extends Player
{

    public static void main(String args[])
    {
        String givenName = "D-Brane v1.1";
        int finalYear = 10000;
        String givenLogPath = "";
        String coalitionString = null;
        InetAddress dipServerIp = null;
        try
        {
            dipServerIp = InetAddress.getByName("localhost");
        }
        catch(UnknownHostException e1)
        {
            e1.printStackTrace();
            return;
        }
        int dipServerPort = 16713;
        boolean negotiates = false;
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].equals("-name") && args.length > i + 1)
                givenName = args[i + 1];
            if(args[i].equals("-fy") && args.length > i + 1)
                finalYear = Integer.parseInt(args[i + 1]);
            if(args[i].equals("-log") && args.length > i + 1)
                givenLogPath = args[i + 1];
            if(args[i].equals("-ip") && args.length > i + 1)
                try
                {
                    dipServerIp = InetAddress.getByName(args[i + 1]);
                }
                catch(UnknownHostException e)
                {
                    System.out.println((new StringBuilder("D-Brane failed to load, because it couldn't create an InetAddress from the given string: ")).append(args[i + 1]).toString());
                    return;
                }
            if(args[i].equals("-port") && args.length > i + 1)
                try
                {
                    dipServerPort = Integer.parseInt(args[i + 1]);
                }
                catch(NumberFormatException e)
                {
                    System.out.println((new StringBuilder("D-Brane failed to load, because it couldn't parse the port number from the given string: ")).append(args[i + 1]).toString());
                    return;
                }
        }

        File LogFolder = new File(givenLogPath);
        LogFolder.mkdirs();
        DBrane_1_1 dBrane = new DBrane_1_1(givenName, givenLogPath, finalYear, negotiates);
        int attempts = 0;
        int maxAttempts = 10;
        for(boolean success = false; !success && attempts < maxAttempts;)
        {
            attempts++;
            success = true;
            try
            {
                es.csic.iiia.fabregues.dip.comm.IComm comm = new DaideComm(dipServerIp, dipServerPort, dBrane.getName());
                dBrane.start(comm);
                dBrane.log.disable();
                dBrane.log.close();
            }
            catch(CommException e)
            {
                e.printStackTrace();
                success = false;
            }
            catch(Throwable e)
            {
                e.printStackTrace();
                success = false;
            }
            if(!success)
                try
                {
                    Thread.sleep(5000L);
                }
                catch(Exception exception) { }
        }

    }

    DBrane_1_1(String givenName, String givenLogPath, int finalYear, String coalitionString)
    {
        super(givenLogPath);
        rand = new Random();
        dbraneNegotiator = null;
        gameStatus = 0;
        homeProvinces = new ArrayList(4);
        availableRegions = new ArrayList();
        rVal2Scs = new ArrayOfLists(20, 10);
        ourUnitsAndEmptyHomeRegions = new RegionSet();
        mValue2Provinces = new ArrayOfLists(10, 10, true);
        name = givenName;
        this.finalYear = finalYear;
        this.coalitionString = coalitionString;
        dBraneCore = new DBraneCore_1_1(this);
    }

    DBrane_1_1(String givenName, String givenLogPath, int finalYear, boolean negotiates)
    {
        super(givenLogPath);
        rand = new Random();
        dbraneNegotiator = null;
        gameStatus = 0;
        homeProvinces = new ArrayList(4);
        availableRegions = new ArrayList();
        rVal2Scs = new ArrayOfLists(20, 10);
        ourUnitsAndEmptyHomeRegions = new RegionSet();
        mValue2Provinces = new ArrayOfLists(10, 10, true);
        name = givenName;
        this.finalYear = finalYear;
        coalitionString = null;
        dBraneCore = new DBraneCore_1_1(this);
    }

    public void init()
    {
        gameStatus = 1;
    }

    public void start()
    {
        DiplomacyInstanceInfo.initializeAgentNames(game.getPowers());
        DiplomacyGame.setPowers(game.getPowers());
        coalitionStructure = new CoalitionStructure(me);
    }

    public List play()
    {
        long phaseStartTime = System.currentTimeMillis();
        Utils.sleepLight(100L);
        long negotiationsDeadLine = phaseStartTime + 14000L;
        long searchDeadLine = phaseStartTime + 14000L + 2000L + 13000L;
        ArrayList myOrders = new ArrayList();
        DiplomacyGame diplomacyGame = new DiplomacyGame(game);
        try
        {
            if(game.getPhase() == Phase.SPR || game.getPhase() == Phase.FAL)
            {
                DBraneWorldState dBraneWorldState = dBraneCore.analyze(diplomacyGame, coalitionStructure);
                for(int donorId = 0; donorId < 7; donorId++)
                {
                    int i = dBraneWorldState.getCoalitionStructure().getCredits(donorId, me.getId());
                }

                ArrayList confirmedDeals = new ArrayList();
                boolean determineAuxiliaryPlans = game.getPhase() == Phase.FAL;
                PlanListWithValue planList = DBraneCore_1_1.searchBestPlan(dBraneWorldState, me, confirmedDeals, determineAuxiliaryPlans, searchDeadLine);
                if(planList == null)
                {
                    myOrders.clear();
                    Region r;
                    for(Iterator iterator1 = me.getControlledRegions().iterator(); iterator1.hasNext(); myOrders.add(new HLDOrder(me, r)))
                        r = (Region)iterator1.next();

                } else
                {
                    ArrayList ourBestPlan = DBraneCore_1_1.processPartialPlans(planList.getPlans(), me);
                    for(Iterator iterator2 = ourBestPlan.iterator(); iterator2.hasNext();)
                    {
                        Order o = (Order)iterator2.next();
                        if(o.getPower().getId() == me.getId())
                            myOrders.add(o);
                    }

                    ExtraOrderFinder.addExtraOrders(diplomacyGame, myOrders, dBraneWorldState.getCoalitionStructure(), confirmedDeals, false);
                }
            } else
            if(game.getPhase() == Phase.SUM || game.getPhase() == Phase.AUT)
            {
                myOrders.addAll(generateRandomRetreats());
            } else
            {
                int nBuilds = me.getOwnedSCs().size() - me.getControlledRegions().size();
                if(nBuilds < 0)
                    myOrders.addAll(generateRemovesBasedOnDistanceToHomes(-nBuilds));
                else
                if(nBuilds > 0)
                    myOrders.addAll(generateBuildOrders(diplomacyGame, coalitionStructure, nBuilds));
            }
        }
        catch(Throwable e)
        {
            myOrders.clear();
            Region r;
            for(Iterator iterator = me.getControlledRegions().iterator(); iterator.hasNext(); myOrders.add(new HLDOrder(me, r)))
                r = (Region)iterator.next();

        }
        return myOrders;
    }

    private List generateRandomRetreats()
    {
        List orders = new Vector(game.getDislodgedRegions().size());
        HashMap units = game.getDislodgedRegions();
        List dislodgedUnits = game.getDislodgedRegions(me);
        for(Iterator iterator = dislodgedUnits.iterator(); iterator.hasNext();)
        {
            Region region = (Region)iterator.next();
            Dislodgement dislodgement = (Dislodgement)units.get(region);
            List dest = new Vector();
            dest.addAll(dislodgement.getRetreateTo());
            if(dest.size() == 0)
            {
                orders.add(new DSBOrder(region, me));
            } else
            {
                int randomInt = rand.nextInt(dest.size());
                orders.add(new RTOOrder(region, me, (Region)dest.get(randomInt)));
            }
        }

        return orders;
    }

    private List generateRandomBuildOrders(int nBuilds)
    {
        List orders = new Vector(nBuilds);
        List homeRegions = new Vector();
        List homeProvinces = new Vector();
        for(Iterator iterator = me.getHomes().iterator(); iterator.hasNext();)
        {
            Province province = (Province)iterator.next();
            if(me.isOwning(province) && !me.isControlling(province))
                homeProvinces.add(province);
        }

        Province province;
        for(Iterator iterator1 = homeProvinces.iterator(); iterator1.hasNext(); homeRegions.addAll(province.getRegions()))
            province = (Province)iterator1.next();

        for(int i = 0; i < nBuilds && homeRegions.size() > 0; i++)
        {
            int randomInt = rand.nextInt(homeRegions.size());
            orders.add(new BLDOrder(me, (Region)homeRegions.get(randomInt)));
            List regionsToRemove = ((Region)homeRegions.get(randomInt)).getProvince().getRegions();
            Region region;
            for(Iterator iterator2 = regionsToRemove.iterator(); iterator2.hasNext(); homeRegions.remove(region))
                region = (Region)iterator2.next();

        }

        for(; orders.size() < nBuilds; orders.add(new WVEOrder(me)));
        return orders;
    }

    private List generateBuildOrders(DiplomacyGame diplomacyGame, CoalitionStructure cs, int nBuilds)
    {
        homeProvinces.clear();
        for(Iterator iterator = me.getHomes().iterator(); iterator.hasNext();)
        {
            Province province = (Province)iterator.next();
            if(me.isOwning(province) && !me.isControlling(province))
                homeProvinces.add(province);
        }

        availableRegions.clear();
        Province province;
        for(Iterator iterator1 = homeProvinces.iterator(); iterator1.hasNext(); availableRegions.addAll(province.getRegions()))
            province = (Province)iterator1.next();

        rVal2Scs.clear();
        int prov2req[] = ExtraOrderFinder.getRequiredReinforcements(diplomacyGame, cs);
        for(Iterator iterator2 = DiplomacyGame.getSupplyCenters().iterator(); iterator2.hasNext();)
        {
            Province sc = (Province)iterator2.next();
            if(prov2req[sc.getId()] > 0)
                rVal2Scs.add(prov2req[sc.getId()], sc);
        }

        ourUnitsAndEmptyHomeRegions.clear();
        Region r;
        for(Iterator iterator3 = me.getControlledRegions().iterator(); iterator3.hasNext(); ourUnitsAndEmptyHomeRegions.add(r))
            r = (Region)iterator3.next();

        Region r;
        for(Iterator iterator4 = availableRegions.iterator(); iterator4.hasNext(); ourUnitsAndEmptyHomeRegions.add(r))
            r = (Region)iterator4.next();

        ArrayOfLists sc2team = ExtraOrderFinder.getReinforcementTeams(prov2req, ourUnitsAndEmptyHomeRegions);
        int province2mValue[] = ExtraOrderFinder.getReinforcementTime(sc2team);
        mValue2Provinces.clear();
        Province sc;
        for(Iterator iterator5 = DiplomacyGame.getSupplyCenters().iterator(); iterator5.hasNext(); mValue2Provinces.add(province2mValue[sc.getId()], sc))
            sc = (Province)iterator5.next();

        int mVal = 1;
        List buildRegions = new ArrayList();
        List removedRegions = new ArrayList();
        while(mVal < 9 && buildRegions.size() < nBuilds) 
        {
            Province sc = (Province)mValue2Provinces.remove(mVal, 0);
            if(sc == null)
            {
                mVal++;
            } else
            {
                Iterator iterator7 = availableRegions.iterator();
                while(iterator7.hasNext()) 
                {
                    Region reg = (Region)iterator7.next();
                    if(!sc2team.get(sc.getId()).contains(reg) || removedRegions.contains(reg))
                        continue;
                    buildRegions.add(reg);
                    Region r;
                    for(Iterator iterator8 = reg.getProvince().getRegions().iterator(); iterator8.hasNext(); removedRegions.add(r))
                        r = (Region)iterator8.next();

                    if(buildRegions.size() == nBuilds)
                        break;
                }
            }
        }
        Region removed;
        for(Iterator iterator6 = removedRegions.iterator(); iterator6.hasNext(); availableRegions.remove(removed))
            removed = (Region)iterator6.next();

        for(int i = 0; i < nBuilds - buildRegions.size() && availableRegions.size() > 0; i++)
        {
            int randomInt = rand.nextInt(availableRegions.size());
            Region buildRegion = (Region)availableRegions.get(randomInt);
            buildRegions.add(buildRegion);
            Region region;
            for(Iterator iterator9 = buildRegion.getProvince().getRegions().iterator(); iterator9.hasNext(); availableRegions.remove(region))
                region = (Region)iterator9.next();

        }

        List orders = new ArrayList(buildRegions.size());
        Region buildReg;
        for(Iterator iterator10 = buildRegions.iterator(); iterator10.hasNext(); orders.add(new BLDOrder(me, buildReg)))
            buildReg = (Region)iterator10.next();

        for(int numWaives = 0; orders.size() < nBuilds; numWaives++)
            orders.add(new WVEOrder(me));

        return orders;
    }

    private List generateRemovesBasedOnDistanceToHomes(int nRemoves)
    {
        List distanceToHomes[] = new List[100];
        for(int i = 0; i < me.getControlledRegions().size(); i++)
        {
            Region unit = (Region)me.getControlledRegions().get(i);
            int d_min = 100;
            int d = 0;
            for(Iterator iterator = DiplomacyGame.getHomeSCs(me).iterator(); iterator.hasNext();)
            {
                int homeId = ((Integer)iterator.next()).intValue();
                Province home = DiplomacyGame.getProvince(homeId);
                d = DiplomacyGame.getDistance(home, unit);
                if(d < d_min)
                    d_min = d;
            }

            if(distanceToHomes[d_min] == null)
                distanceToHomes[d_min] = new ArrayList(5);
            distanceToHomes[d_min].add(unit);
        }

        List orders = new ArrayList(nRemoves);
        int d = distanceToHomes.length - 1;
        while(orders.size() < nRemoves) 
            if(distanceToHomes[d] == null || distanceToHomes[d].size() == 0)
            {
                d--;
            } else
            {
                Region region = (Region)distanceToHomes[d].remove(0);
                orders.add(new REMOrder(me, region));
            }
        return orders;
    }

    private List generateRemovesBasedOnLocationType(int nRemoves)
    {
        List unitsSorted = new ArrayList();
        for(int i = 0; i < me.getControlledRegions().size(); i++)
            if(!((Region)me.getControlledRegions().get(i)).getProvince().isSC())
                unitsSorted.add((Region)me.getControlledRegions().get(i));

        for(int i = 0; i < me.getControlledRegions().size(); i++)
        {
            Province province = ((Region)me.getControlledRegions().get(i)).getProvince();
            if(province.isSC() && !DiplomacyGame.getHomeSCs(me).contains(province))
                unitsSorted.add((Region)me.getControlledRegions().get(i));
        }

        for(int i = 0; i < me.getControlledRegions().size(); i++)
        {
            Province province = ((Region)me.getControlledRegions().get(i)).getProvince();
            if(DiplomacyGame.getHomeSCs(me).contains(province))
                unitsSorted.add((Region)me.getControlledRegions().get(i));
        }

        List orders = new ArrayList(nRemoves);
        for(int i = 0; i < nRemoves; i++)
            orders.add(new REMOrder(me, (Region)unitsSorted.get(i)));

        return orders;
    }

    public void submissionError(String message[])
    {
        String response = message[message.length - 2];
    }

    public void receivedOrder(Order order1)
    {
    }

    public void handleCCD(String string)
    {
        exit();
    }

    public void handleSlo(String winner)
    {
        gameStatus = 2;
    }

    public void handleSMR(String message[])
    {
        if(gameStatus != 2)
            gameStatus = 3;
        exit();
        System.exit(0);
    }

    public void phaseEnd(GameState gameState)
    {
        if(game.getYear() == finalYear && game.getPhase() == Phase.FAL || game.getYear() > finalYear)
            proposeDraw();
        super.phaseEnd(gameState);
    }

    public void exit()
    {
        try
        {
            if(dbraneNegotiator != null)
                dbraneNegotiator.closeConnection();
            super.exit();
        }
        catch(IOException e)
        {
            System.exit(0x1e240);
        }
    }

    void proposeDraw()
    {
        try
        {
            comm.sendMessage(new String[] {
                "DRW"
            });
        }
        catch(CommException e)
        {
            e.printStackTrace();
        }
    }

    public static final int NO_GAME_ACTIVE = 0;
    public static final int GAME_ACTIVE = 1;
    public static final int GAME_ENDED_WITH_SOLO = 2;
    public static final int GAME_ENDED_WITH_DRAW = 3;
    public static final String type = "D-Brane";
    public static final String version = "v1.1";
    public static final int DEFAULT_PORT = 16713;
    static final int NEGOTIATION_LENGTH = 14000;
    static final int AFTER_NEGO_LENGTH = 2000;
    static final int SEARCH_LENGTH = 13000;
    static final int DEFAULT_FINAL_YEAR = 10000;
    private InetAddress negoServerIp;
    static int negoServerPort = 16714;
    private int finalYear;
    final boolean negotiates = false;
    final boolean obeyImplicitAgreements = false;
    private Random rand;
    DBraneCore_1_1 dBraneCore;
    final DBraneNegotiator dbraneNegotiator;
    String coalitionString;
    CoalitionStructure coalitionStructure;
    int gameStatus;
    private List homeProvinces;
    private List availableRegions;
    private ArrayOfLists rVal2Scs;
    private RegionSet ourUnitsAndEmptyHomeRegions;
    private ArrayOfLists mValue2Provinces;

}
