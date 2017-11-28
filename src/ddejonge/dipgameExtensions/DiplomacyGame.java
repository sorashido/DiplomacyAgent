// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DiplomacyGame.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.*;
import java.io.PrintStream;
import java.util.*;

// Referenced classes of package ddejonge.dipgameExtensions:
//            ProvinceSet, RegionSet, PowerSet

public class DiplomacyGame
{

    public static void setPowers(List powers)
    {
        for(Iterator iterator = powers.iterator(); iterator.hasNext();)
        {
            Power pow = (Power)iterator.next();
            allPowers[pow.getId()] = pow;
        }

    }

    public DiplomacyGame(Game game)
    {
        unitsOf = new RegionSet[7];
        controller = new Power[120];
        province2controller = new Power[75];
        province2owner = new Power[75];
        this.game = game;
        setPowers(game.getPowers());
        for(Iterator iterator = game.getRegions().iterator(); iterator.hasNext();)
        {
            Region r = (Region)iterator.next();
            if(r.getId() > allRegions.length)
                System.err.println("NUMBER OF REGIONS INCORRECT!");
            allRegions[r.getId()] = r;
        }

        supplyCentersList = new ArrayList(34);
        for(Iterator iterator1 = game.getProvinces().iterator(); iterator1.hasNext();)
        {
            Province p = (Province)iterator1.next();
            province2itsRegions[p.getId()] = new RegionSet();
            allProvinces[p.getId()] = p;
            if(p.isSC())
            {
                supplyCenters.add(p);
                supplyCentersList.add(p);
            }
            Region r;
            for(Iterator iterator2 = p.getRegions().iterator(); iterator2.hasNext(); province2itsRegions[p.getId()].add(r))
                r = (Region)iterator2.next();

        }

        if(!mapsInitialized)
        {
            calculateDistances();
            fillNeighborsMap();
            fill2ndOrderNeighborsMaps();
            fillIntermediateRegionsMap();
            fillSecondOrderInclusiveMap();
            fillPower2HomeSCs();
            mapsInitialized = true;
        }
        fillMaps();
    }

    private void fillMaps()
    {
        fillUnitsMap();
        fillAdjacentUnitsMap();
        fillOwnerMap();
    }

    private static void calculateDistances()
    {
        for(int i = 0; i < 120; i++)
            if(isFleetRegion(allRegions[i]))
                fleetRegions.add(allRegions[i]);
            else
                armyRegions.add(allRegions[i]);

        fleetPaths = floyds(fleetRegions);
        armyPaths = floyds(armyRegions);
        for(int i = 1; i < 65; i++)
            fleetRegion2number.put(((Region)fleetRegions.get(i)).toString(), Integer.valueOf(i));

        for(int i = 1; i < 57; i++)
            armyRegion2number.put(((Region)armyRegions.get(i)).toString(), Integer.valueOf(i));

    }

    private static void fillNeighborsMap()
    {
        Region aregion[];
        int j = (aregion = allRegions).length;
        for(int i = 0; i < j; i++)
        {
            Region r = aregion[i];
            region2neighbors[r.getId()] = new RegionSet();
            Region r2;
            for(Iterator iterator = r.getAdjacentRegions().iterator(); iterator.hasNext(); region2neighbors[r.getId()].add(r2))
                r2 = (Region)iterator.next();

        }

        Province aprovince[];
        int l = (aprovince = allProvinces).length;
        for(int k = 0; k < l; k++)
        {
            Province p = aprovince[k];
            RegionSet neighsOfProv = new RegionSet();
            Region r;
            for(Iterator iterator1 = p.getRegions().iterator(); iterator1.hasNext(); neighsOfProv.addAll(region2neighbors[r.getId()]))
                r = (Region)iterator1.next();

            province2neighbors[p.getId()] = neighsOfProv;
        }

    }

    private static void fill2ndOrderNeighborsMaps()
    {
        for(Iterator iterator = fleetRegions.iterator(); iterator.hasNext();)
        {
            Region r1 = (Region)iterator.next();
            if(r1 != null)
            {
                RegionSet set = new RegionSet();
                ArrayList list = new ArrayList();
                for(Iterator iterator2 = fleetRegions.iterator(); iterator2.hasNext();)
                {
                    Region r2 = (Region)iterator2.next();
                    if(r2 != null && getDistance(r1, r2) == 2)
                    {
                        set.add(r2);
                        list.add(r2);
                    }
                }

                region2secondOrderNeighbors[r1.getId()] = set;
            }
        }

        for(Iterator iterator1 = armyRegions.iterator(); iterator1.hasNext();)
        {
            Region r1 = (Region)iterator1.next();
            if(r1 != null)
            {
                RegionSet set = new RegionSet();
                ArrayList list = new ArrayList();
                for(Iterator iterator3 = armyRegions.iterator(); iterator3.hasNext();)
                {
                    Region r2 = (Region)iterator3.next();
                    if(r2 != null && getDistance(r1, r2) == 2)
                    {
                        set.add(r2);
                        list.add(r2);
                    }
                }

                region2secondOrderNeighbors[r1.getId()] = set;
            }
        }

        Province aprovince[];
        int j = (aprovince = allProvinces).length;
        for(int i = 0; i < j; i++)
        {
            Province p = aprovince[i];
            RegionSet set = new RegionSet();
            Region regionInsideP;
            for(Iterator iterator4 = p.getRegions().iterator(); iterator4.hasNext(); set.addAll(region2secondOrderNeighbors[regionInsideP.getId()]))
                regionInsideP = (Region)iterator4.next();

            province2secondOrderNeighbors[p.getId()] = set;
        }

    }

    private static void fillSecondOrderInclusiveMap()
    {
        for(int p = 0; p < 75; p++)
        {
            province2secOrderNeighborsInclusive[p] = new RegionSet();
            for(int r = 0; r < 120; r++)
                if(getDistance(allProvinces[p], allRegions[r]) <= 2)
                    province2secOrderNeighborsInclusive[p].add(allRegions[r]);

        }

    }

    private void fillPower2HomeSCs()
    {
        for(Iterator iterator = game.getPowers().iterator(); iterator.hasNext();)
        {
            Power pow = (Power)iterator.next();
            ProvinceSet set = new ProvinceSet();
            Province prov;
            for(Iterator iterator1 = pow.getHomes().iterator(); iterator1.hasNext(); set.add(prov))
                prov = (Province)iterator1.next();

            power2homeSCs[pow.getId()] = set;
        }

    }

    private static void fillIntermediateRegionsMap()
    {
        Province aprovince[];
        int j = (aprovince = allProvinces).length;
        for(int i = 0; i < j; i++)
        {
            Province p = aprovince[i];
            RegionSet neighsOfRegion = null;
            RegionSet neighsOfProvince = null;
            Region aregion[];
            int l = (aregion = allRegions).length;
            for(int k = 0; k < l; k++)
            {
                Region r = aregion[k];
                if(getDistance(p, r) <= 2)
                {
                    neighsOfRegion = region2neighbors[r.getId()];
                    neighsOfProvince = province2neighbors[p.getId()];
                    RegionSet set = new RegionSet();
                    ArrayList list = new ArrayList();
                    for(Iterator iterator = neighsOfRegion.iterator(); iterator.hasNext();)
                    {
                        Region reg = (Region)iterator.next();
                        if(neighsOfProvince.contains(reg))
                        {
                            set.add(reg);
                            list.add(reg);
                        }
                    }

                    provAndRegion2intermediateRegionsList[p.getId()][r.getId()] = list;
                }
            }

        }

    }

    private void fillUnitsMap()
    {
        Power apower[];
        int j = (apower = allPowers).length;
        for(int i = 0; i < j; i++)
        {
            Power pow = apower[i];
            RegionSet units = new RegionSet();
            for(Iterator iterator = pow.getControlledRegions().iterator(); iterator.hasNext();)
            {
                Region unit = (Region)iterator.next();
                units.add(unit);
                controller[unit.getId()] = pow;
                province2controller[unit.getProvince().getId()] = pow;
            }

            unitsOf[pow.getId()] = units;
        }

    }

    private void fillAdjacentUnitsMap()
    {
        Province aprovince[];
        int j = (aprovince = allProvinces).length;
        for(int i = 0; i < j; i++)
        {
            Province p = aprovince[i];
            RegionSet adjacentUnits = new RegionSet();
            Region r;
            for(Iterator iterator = game.getAdjacentUnits(p).iterator(); iterator.hasNext(); adjacentUnits.add(r))
                r = (Region)iterator.next();

            province2adjacentUnits[p.getId()] = adjacentUnits;
        }

    }

    private void fillOwnerMap()
    {
        Province aprovince[];
        int j = (aprovince = allProvinces).length;
        for(int i = 0; i < j; i++)
        {
            Province p = aprovince[i];
            province2owner[p.getId()] = game.getOwner(p);
        }

    }

    public static RegionSet getNeighbors(Province p)
    {
        return province2neighbors[p.getId()];
    }

    public static RegionSet getNeighbors(Region r)
    {
        return region2neighbors[r.getId()];
    }

    public static RegionSet getSecondOrderNeighbors(Province p)
    {
        return province2secondOrderNeighbors[p.getId()];
    }

    public static RegionSet getSecondOrderNeighbors(Region r)
    {
        return region2secondOrderNeighbors[r.getId()];
    }

    public static RegionSet getSecondOrderNeighborsInclusive(Province p)
    {
        return province2secOrderNeighborsInclusive[p.getId()];
    }

    public Region[] getAllRegions()
    {
        return allRegions;
    }

    public PowerSet getAllPowers()
    {
        return new PowerSet(true);
    }

    public static List getIntermediateRegionsList(Province sc, Region r)
    {
        return provAndRegion2intermediateRegionsList[sc.getId()][r.getId()];
    }

    public RegionSet getUnitsOf(Power power)
    {
        return unitsOf[power.getId()];
    }

    public RegionSet getUnitsOf(int powerID)
    {
        return unitsOf[powerID];
    }

    public Power getController(Region region)
    {
        return controller[region.getId()];
    }

    public Power getController(Province province)
    {
        return province2controller[province.getId()];
    }

    public Power getController(int provinceId)
    {
        return province2controller[provinceId];
    }

    public RegionSet getAdjacentUnits(Province p)
    {
        return province2adjacentUnits[p.getId()];
    }

    public Power getOwner(Province p)
    {
        return province2owner[p.getId()];
    }

    public static Power getPower(int id)
    {
        return allPowers[id];
    }

    public static Power getPower(String name)
    {
        Power apower[];
        int j = (apower = allPowers).length;
        for(int i = 0; i < j; i++)
        {
            Power p = apower[i];
            if(p.getName().equals(name))
                return p;
        }

        return null;
    }

    public static Region getRegion(int id)
    {
        return allRegions[id];
    }

    public static Region getRegion(String name)
    {
        Region aregion[];
        int j = (aregion = allRegions).length;
        for(int i = 0; i < j; i++)
        {
            Region r = aregion[i];
            if(r.getName().equals(name))
                return r;
        }

        return null;
    }

    public static Province getProvince(int id)
    {
        return allProvinces[id];
    }

    public static Province getProvince(String name)
    {
        Province aprovince[];
        int j = (aprovince = allProvinces).length;
        for(int i = 0; i < j; i++)
        {
            Province p = aprovince[i];
            if(p.getName().equals(name))
                return p;
        }

        return null;
    }

    public static ArrayList getSupplyCenters()
    {
        return supplyCentersList;
    }

    public static ProvinceSet getSupplyCenterSet()
    {
        return supplyCenters;
    }

    public static ProvinceSet getHomeSCs(Power power)
    {
        return power2homeSCs[power.getId()];
    }

    public static int getDistance(Region r1, Region r2)
    {
        boolean isFleet1 = isFleetRegion(r1);
        boolean isFleet2 = isFleetRegion(r2);
        if(isFleet1 != isFleet2)
            return 1000;
        if(isFleet1 && isFleet2)
        {
            int i = ((Integer)fleetRegion2number.get(r1.toString())).intValue();
            int j = ((Integer)fleetRegion2number.get(r2.toString())).intValue();
            return fleetPaths[i][j][64];
        } else
        {
            int i = ((Integer)armyRegion2number.get(r1.toString())).intValue();
            int j = ((Integer)armyRegion2number.get(r2.toString())).intValue();
            return armyPaths[i][j][56];
        }
    }

    public static int getDistance(Province p, Region r)
    {
        int min = 1000;
        int distance = min;
        for(Iterator iterator = p.getRegions().iterator(); iterator.hasNext();)
        {
            Region r1 = (Region)iterator.next();
            distance = getDistance(r1, r);
            if(distance < min)
                min = distance;
        }

        return min;
    }

    public static int getDistance(Province p0, Province p1)
    {
        int min = 1000;
        int distance = min;
        for(Iterator iterator = p0.getRegions().iterator(); iterator.hasNext();)
        {
            Region r0 = (Region)iterator.next();
            for(Iterator iterator1 = p1.getRegions().iterator(); iterator1.hasNext();)
            {
                Region r1 = (Region)iterator1.next();
                distance = getDistance(r0, r1);
                if(distance < min)
                    min = distance;
            }

        }

        return min;
    }

    public static void displayAllDistances()
    {
        for(int i = 1; i < 65; i++)
        {
            for(int j = i + 1; j < 65; j++)
                System.out.println((new StringBuilder(String.valueOf(((Region)fleetRegions.get(i)).getName()))).append(" - ").append(((Region)fleetRegions.get(j)).getName()).append(" ").append(fleetPaths[i][j][64]).toString());

        }

        for(int i = 1; i < 57; i++)
        {
            for(int j = i + 1; j < 57; j++)
                System.out.println((new StringBuilder(String.valueOf(((Region)armyRegions.get(i)).getName()))).append(" - ").append(((Region)armyRegions.get(j)).getName()).append(" ").append(armyPaths[i][j][56]).toString());

        }

    }

    private static boolean isFleetRegion(Region r)
    {
        return r.getName().endsWith("FLT") || r.getName().endsWith("CS");
    }

    private static boolean isAdjacent(Vector regions, int i, int j)
    {
        return ((Region)regions.get(i)).getAdjacentRegions().contains(regions.get(j));
    }

    public static void displayAllRegionNames()
    {
        Region r;
        for(Iterator iterator = armyRegions.iterator(); iterator.hasNext(); System.out.println(r.getName()))
            r = (Region)iterator.next();

        Region r;
        for(Iterator iterator1 = fleetRegions.iterator(); iterator1.hasNext(); System.out.println(r.getName()))
            r = (Region)iterator1.next();

    }

    public void displayAllProvinceNames()
    {
        for(Iterator iterator = game.getProvinces().iterator(); iterator.hasNext(); System.out.println())
        {
            Province p = (Province)iterator.next();
            System.out.print(p.getName());
            if(p.isSC())
                System.out.print("(SC)");
        }

    }

    public static int[][][] floyds(Vector regions)
    {
        int numRegions = regions.size();
        regions.insertElementAt(null, 0);
        int shortestPath[][][] = new int[numRegions + 1][numRegions + 1][numRegions + 1];
        for(int i = 0; i < numRegions + 1; i++)
        {
            for(int j = 0; j < numRegions + 1; j++)
            {
                for(int k = 0; k < numRegions + 1; k++)
                {
                    shortestPath[i][j][k] = 1000;
                    if(i == j)
                        shortestPath[i][j][k] = 0;
                }

            }

        }

        for(int i = 1; i < numRegions + 1; i++)
        {
            for(int j = i + 1; j < numRegions + 1; j++)
                if(isAdjacent(regions, i, j))
                {
                    shortestPath[i][j][0] = 1;
                    shortestPath[j][i][0] = 1;
                }

        }

        for(int k = 1; k < numRegions + 1; k++)
        {
            for(int i = 1; i < numRegions + 1; i++)
            {
                for(int j = i + 1; j < numRegions + 1; j++)
                {
                    shortestPath[i][j][k] = Math.min(shortestPath[i][j][k - 1], shortestPath[i][k][k - 1] + shortestPath[k][j][k - 1]);
                    shortestPath[j][i][k] = shortestPath[i][j][k];
                }

            }

        }

        return shortestPath;
    }

    public static final int NUM_FLEET_REGIONS = 64;
    public static final int NUM_ARMY_REGIONS = 56;
    public static final int NUM_REGIONS = 120;
    public static final int NUM_SUPPLY_CENTERS = 34;
    public static final int NUM_PROVINCES = 75;
    public static final int NUM_POWERS = 7;
    static final int MAX_DISTANCE = 1000;
    private static boolean mapsInitialized = false;
    static Region allRegions[] = new Region[120];
    private static Province allProvinces[] = new Province[75];
    private static Power allPowers[] = new Power[7];
    static ProvinceSet supplyCenters = new ProvinceSet();
    static int fleetPaths[][][];
    static int armyPaths[][][];
    static Vector fleetRegions = new Vector(64);
    static Vector armyRegions = new Vector(56);
    static HashMap fleetRegion2number = new HashMap(64);
    static HashMap armyRegion2number = new HashMap(56);
    static RegionSet region2neighbors[] = new RegionSet[120];
    static RegionSet region2secondOrderNeighbors[] = new RegionSet[120];
    static RegionSet province2neighbors[] = new RegionSet[75];
    static RegionSet province2secondOrderNeighbors[] = new RegionSet[75];
    static RegionSet province2secOrderNeighborsInclusive[] = new RegionSet[75];
    static ArrayList supplyCentersList = new ArrayList(34);
    static RegionSet province2adjacentUnits[] = new RegionSet[75];
    static RegionSet province2itsRegions[] = new RegionSet[75];
    static ProvinceSet power2homeSCs[] = new ProvinceSet[7];
    public Game game;
    RegionSet unitsOf[];
    Power controller[];
    Power province2controller[];
    Power province2owner[];
    static ArrayList provAndRegion2intermediateRegionsList[][] = new ArrayList[75][120];

}
