package sorashido.DDAgent3.negotiation;

import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.HLDOrder;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;
import sorashido.DDAgent3.model.UtilityModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProposeDeal {

    public  Random random = new Random();
    public UtilityModel utilityModel = new UtilityModel();

    public BasicDeal searchForNewDealToPropose(Game game, Power me, DBraneTactics dBraneTactics, List<BasicDeal> commitments, List<Power> aliveNegotiatingPowers){

        BasicDeal bestDeal = null;
        Plan bestPlan = null;

        //First, let's see what happens if we do not make any new commitments.
        bestPlan = dBraneTactics.determineBestPlan(game, me, commitments);

        //If our current commitments are already inconsistent then we certainly
        // shouldn't make any more commitments.
        if(bestPlan == null){
            return null;
        }

        //let's generate 10 random deals and pick the best one.
        for(int i=0; i<10; i++){

            //generate a random deal.
            BasicDeal randomDeal = generateRandomDeal(game, me, aliveNegotiatingPowers);

            if(randomDeal == null){
                continue;
            }


            //add it to the list containing our existing commitments so that dBraneTactics can determine a plan.
            commitments.add(randomDeal);


            //Ask the D-Brane Tactical Module what it would do under these commitments.
            Plan plan = dBraneTactics.determineBestPlan(game, me, commitments);

            //Check if the returned plan is better than the best plan found so far.
            utilityModel.calcPlanValue(game, me, dBraneTactics, (ArrayList<BasicDeal>) commitments);

            if(plan != null && plan.getValue() > bestPlan.getValue()){
                bestPlan = plan;
                bestDeal = randomDeal;
            }

            //Remove the randomDeal from the list, for the next iteration.
            commitments.remove(commitments.size()-1);
        }


        return bestDeal;
    }

    public BasicDeal generateRandomDeal(Game game, Power me, List<Power> aliveNegotiatingPowers){
        //if there are less than 2 negotiating powers left alive (only me), then it makes no sense to negotiate.
        int numAliveNegoPowers = aliveNegotiatingPowers.size();
        if(numAliveNegoPowers < 2){
            return null;
        }

        //Let's generate 3 random demilitarized zones.
        List<DMZ> demilitarizedZones = new ArrayList<DMZ>(3);
        for(int i=0; i<3; i++){

            //1. Create a list of powers
            ArrayList<Power> powers = new ArrayList<Power>(2);

            //1a. add myself to the list
            powers.add(me);

            //1b. add a random other power to the list.
            Power randomPower = me;
            while(randomPower.equals(me)){

                int numNegoPowers = aliveNegotiatingPowers.size();
                randomPower = aliveNegotiatingPowers.get(random.nextInt(numNegoPowers));
            }
            powers.add(randomPower);

            //2. Create a list containing 3 random provinces.
            ArrayList<Province> provinces = new ArrayList<Province>();
            for(int j=0; j<3; j++){
                int numProvinces = game.getProvinces().size();
                Province randomProvince = game.getProvinces().get(random.nextInt(numProvinces));
                provinces.add(randomProvince);
            }

            //This agent only generates deals for the current year and phase.
            // However, you can pick any year and phase here, as long as they do not lie in the past.
            // (actually, you can also propose deals for rounds in the past, but it doesn't make any sense
            //  since you obviously cannot obey such deals).
            demilitarizedZones.add(new DMZ( game.getYear(), game.getPhase(), powers, provinces));

        }

        //let's generate 3 random OrderCommitments
        List<OrderCommitment> randomOrderCommitments = new ArrayList<OrderCommitment>();


        //get all units of the negotiating powers.
        List<Region> units = new ArrayList<Region>();
        for(Power power : aliveNegotiatingPowers){
            units.addAll(power.getControlledRegions());
        }

        for(int i=0; i<3; i++){

            //Pick a random unit and remove it from the list
            if(units.size() == 0){
                break;
            }
            Region randomUnit = units.remove(random.nextInt(units.size()));

            //Get the corresponding power
            Power power = game.getController(randomUnit);

            //Determine a list of potential destinations for the unit.
            // a Region is a potential destination for a unit if it is adjacent to that unit (or it is the current location of the unit)
            //  and the Province is not demilitarized for the Power controlling that unit.
            List<Region> potentialDestinations = new ArrayList<Region>();

            //Create a list of adjacent regions, including the current location of the unit.
            List<Region> adjacentRegions = new ArrayList<>(randomUnit.getAdjacentRegions());
            adjacentRegions.add(randomUnit);

            for(Region adjacentRegion : adjacentRegions){

                Province adjacentProvince = adjacentRegion.getProvince();

                //Check that the adjacent Region is not demilitarized for the power controlling the unit.
                boolean isDemilitarized = false;
                for(DMZ dmz : demilitarizedZones){
                    if(dmz.getPowers().contains(power) && dmz.getProvinces().contains(adjacentProvince)){
                        isDemilitarized = true;
                        break;
                    }

                }

                //If it is not demilitarized, then we can add the region to the list of potential destinations.
                if(!isDemilitarized){
                    potentialDestinations.add(adjacentRegion);
                }
            }

            int numPotentialDestinations = potentialDestinations.size();
            if(numPotentialDestinations > 0){

                Region randomDestination = potentialDestinations.get(random.nextInt(numPotentialDestinations));

                Order randomOrder;
                if(randomDestination.equals(randomUnit)){
                    randomOrder = new HLDOrder(power, randomUnit);
                }else{
                    randomOrder = new MTOOrder(power, randomUnit, randomDestination);
                }
                // Of course we could also propose random support orders, but we don't do that here.


                //We only generate deals for the current year and phase.
                // However, you can pick any year and phase here, as long as they do not lie in the past.
                // (actually, you can also propose deals for rounds in the past, but it doesn't make any sense
                //  since you obviously cannot obey such deals).
                randomOrderCommitments.add(new OrderCommitment(game.getYear(), game.getPhase(), randomOrder));
            }

        }
        BasicDeal deal = new BasicDeal(randomOrderCommitments, demilitarizedZones);
        return deal;
    }
}
