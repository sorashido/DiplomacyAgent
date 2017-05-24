package ddejonge.bandana.DDAgent;

import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.exampleAgents.ANACExampleNegotiator;
import ddejonge.bandana.negoProtocol.*;
import ddejonge.bandana.tools.Utilities;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.HLDOrder;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tela on 2017/05/09.
 */
public class DDAgent extends ANACNegotiator{

    /**
     * Main method to start the agent.
     *
     * This player can be started with the following arguments:
     * -name  	[the name of your agent]
     * -log		[the path to the folder where you want the log files to be stored]
     * -fy 		[the year after which your agent will propose a draw]
     * -gamePort  [the port of the game server]
     * -negoPort  [the port of the negotiation server]
     *
     * e.g. java -jar ANACExampleNegotiator.jar -name alice -log C:\\documents\log -fy 1920 -gamePort 16713 -negoPort 16714
     *
     * All of these arguments are optional.
     *
     * Note however that during the competition the values of these arguments will be chosen by the organizers
     * of the competition, so you can only control them during the development of your negotiator.
     *
     * @param args
     */
    public static void main(String[] args){
        DDAgent myPlayer = new DDAgent(args);

        myPlayer.run();
    }

    public Random random = new Random();
    DBraneTactics dBraneTactics = new DBraneTactics();

    //Constructor
    /**
     * You must implement a Constructor with exactly this signature.
     * The body of the Constructor must start with the line <code>super(args)</code>
     * but below that line you can put whatever you like.
     * @param args
     */
    public DDAgent(String[] args) {
        super(args);

        dBraneTactics = this.getTacticalModule();
    }

    /**
     * This method is automatically called at the start of the game, after the 'game' field is set.
     *
     * It is called when the first NOW message is received from the game server.
     * The NOW message contains the current phase and the positions of all the units.
     *
     * You are allowed, but not required, to implement this method
     *
     */
    @Override
    public void start() {
        //You can use the logger to write stuff to the log file.
        //The location of the log file can be set through the command line option -log.
        // it is not necessary to call getLogger().enable() because this is already automatically done by the ANACNegotiator class.

        boolean printToConsole = true; //if set to true the text will be written to file, as well as printed to the standard output stream. If set to false it will only be written to file.
        this.getLogger().logln("game is starting!", printToConsole);
    }

    @Override
    public void negotiate(long negotiationDeadline) {
        List<BasicDeal> newDealToProposes = null; //提案候補

        System.out.println();

        //交渉の間, ループし続ける
        while(System.currentTimeMillis() < negotiationDeadline){
            long restTime = negotiationDeadline - System.currentTimeMillis(); //残り時間 ms
            double threshold = 0.8; //効用値 想定() 統計的に表すことが可能?

            //1. 自分が相手に提案
            //1.1 各国それぞれへの提案の候補を個別に探索 (自分と相手の効用値に基づいて探索, (効用値は線形に妥協しても良い = \alpha \betaの値))


            //1.2 それぞれの候補で矛盾がない組み合わせ最適化!!



            //2. 送られてきたメッセージを処理
            while(hasMessage()){
                //2.1 効用値を計算し, ある一定以上であれば許可


            }


            //3. 引き分けを提案
        }
    }

    List<BasicDeal> searchForNewDealToPropose(Power opponent, int m, double threshold, double myParam, double opParam) {
        List<BasicDeal> bestDeals = null;
        Plan bestPlan = null;

        List<BasicDeal> commitments = this.getConfirmedDeals(); //現在の取り決め
        bestPlan = this.dBraneTactics.determineBestPlan(game, me, commitments); //取引なしの場合のbestPlan

        if (bestPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
            return null;
        }

        //opponent, army毎にどこに移動するのが一番かを計算
        //List<BasicDeal> baseDeal = generateOrderDeal(opponent, army, myParam, opParam);

        //opponent毎にどんな条約を結びたいかを計算
        //List<BasicDeal> baseDeal = generateDMZ(opponent, army, myParam, opParam);

        //Listに合わせてreturn



//      1.2 関係に合わせて相手の効用値を考える(関係が強いほど足し合わせた効用値が大事)
//      Plan plan1 = this.dBraneTactics.determineBestPlan(game, opponent, commitments);
//      a = plan1.getValue() * myParam
//      Plan plan2 = this.dBraneTactics.determineBestPlan(game, me, commitments);
//      b= plan.getValue() * opParam
//      if(a+b > threshold) add bestDeals(deal)

        // plan.getMyOrders() では planのもとでdbraneが選択するオーダーを求めることが可能.
        // このオーダーをもとに効用値を自分で定義することが可能!
        //Get a copy of our list of current commitments.

//        //First, let's see what happens if we do not make any new commitments.
//        bestPlan = this.dBraneTactics.determineBestPlan(game, me, commitments);
//
//        //If our current commitments are already inconsistent then we certainly
//        // shouldn't make any more commitments.
//        if(bestPlan == null){
//            return null;
//        }
//
//        //let's generate 10 random deals and pick the best one.
//        for(int i=0; i<10; i++){
//
//            //generate a random deal.
//            BasicDeal randomDeal = generateRandomDeal();
//
//            if(randomDeal == null){
//                continue;
//            }
//
//
//            //add it to the list containing our existing commitments so that dBraneTactics can determine a plan.
//            commitments.add(randomDeal);
//
//
//            //Ask the D-Brane Tactical Module what it would do under these commitments.
//            Plan plan = this.dBraneTactics.determineBestPlan(game, me, commitments);
//
//            //Check if the returned plan is better than the best plan found so far.
//            if(plan != null && plan.getValue() > bestPlan.getValue()){
//                bestPlan = plan;
//                bestDeal = randomDeal;
//            }
//
//
//            //Remove the randomDeal from the list, for the next iteration.
//            commitments.remove(commitments.size()-1);
//
//
//            //NOTE: the value returned by plan.getValue() represents the number of Supply Centers that the D-Brane Tactical Module
//            // expects to conquer in the current round under the given commitments.
//            //
//            // Of course, this is only a rough indication of which plan is truly the "best". After all, sometimes it is better
//            // not to try to conquer as many Supply Centers as you can directly, but rather organize your armies and only attack in a later
//            // stage.
//            // Therefore, you may want to implement your own algorithm to determine which plan is the best.
//            // You can call plan.getMyOrders() to retrieve the complete list of orders that D-Brane has chosen for you under the given commitments.
//
//

        return bestDeals;
    }

//    public BasicDeal generateRandomDeal(Power opponent){
//
//
//        //Get the names of all the powers that are connected to the negotiation server and which have not been eliminated.
//        List<Power> aliveNegotiatingPowers = this.getNegotiatingPowers();
//        if(!aliveNegotiatingPowers.contains(opponent)){
//            return null;
//        }
//
//        //if there are less than 2 negotiating powers left alive (only me), then it makes no sense to negotiate.
//        int numAliveNegoPowers = aliveNegotiatingPowers.size();
//        if(numAliveNegoPowers < 2){
//            return null;
//        }
//
//        //Let's generate 3 random demilitarized zones.
//        List<DMZ> demilitarizedZones = new ArrayList<DMZ>(3);
//        for(int i=0; i<3; i++){
//
//            //1. Create a list of powers
//            ArrayList<Power> powers = new ArrayList<Power>(2);
//
//            //1a. add myself to the list
//            powers.add(me);
//
//            //1b. add a random other power to the list.
//            Power randomPower = me;
//            while(randomPower.equals(me)){
//
//                int numNegoPowers = aliveNegotiatingPowers.size();
//                randomPower = aliveNegotiatingPowers.get(random.nextInt(numNegoPowers));
//            }
//            powers.add(randomPower);
//
//            //2. Create a list containing 3 random provinces.
//            ArrayList<Province> provinces = new ArrayList<Province>();
//            for(int j=0; j<3; j++){
//                int numProvinces = this.game.getProvinces().size();
//                Province randomProvince = this.game.getProvinces().get(random.nextInt(numProvinces));
//                provinces.add(randomProvince);
//            }
//
//
//            //This agent only generates deals for the current year and phase.
//            // However, you can pick any year and phase here, as long as they do not lie in the past.
//            // (actually, you can also propose deals for rounds in the past, but it doesn't make any sense
//            //  since you obviously cannot obey such deals).
//            demilitarizedZones.add(new DMZ( game.getYear(), game.getPhase(), powers, provinces));
//
//            //let's generate 3 random OrderCommitments
//            List<OrderCommitment> randomOrderCommitments = new ArrayList<OrderCommitment>();
//
//
//            //get all units of the negotiating powers.
//            List<Region> units = new ArrayList<Region>();
//            for(Power power : aliveNegotiatingPowers){
//                units.addAll(power.getControlledRegions());
//            }
//
//
//            for(int i=0; i<3; i++){
//
//                //Pick a random unit and remove it from the list
//                if(units.size() == 0){
//                    break;
//                }
//                Region randomUnit = units.remove(random.nextInt(units.size()));
//
//                //Get the corresponding power
//                Power power = game.getController(randomUnit);
//
//                //Determine a list of potential destinations for the unit.
//                // a Region is a potential destination for a unit if it is adjacent to that unit (or it is the current location of the unit)
//                //  and the Province is not demilitarized for the Power controlling that unit.
//                List<Region> potentialDestinations = new ArrayList<Region>();
//
//                //Create a list of adjacent regions, including the current location of the unit.
//                List<Region> adjacentRegions = new ArrayList<>(randomUnit.getAdjacentRegions());
//                adjacentRegions.add(randomUnit);
//
//                for(Region adjacentRegion : adjacentRegions){
//
//                    Province adjacentProvince = adjacentRegion.getProvince();
//
//                    //Check that the adjacent Region is not demilitarized for the power controlling the unit.
//                    boolean isDemilitarized = false;
//                    for(DMZ dmz : demilitarizedZones){
//                        if(dmz.getPowers().contains(power) && dmz.getProvinces().contains(adjacentProvince)){
//                            isDemilitarized = true;
//                            break;
//                        }
//
//                    }
//
//                    //If it is not demilitarized, then we can add the region to the list of potential destinations.
//                    if(!isDemilitarized){
//                        potentialDestinations.add(adjacentRegion);
//                    }
//                }
//
//
//                int numPotentialDestinations = potentialDestinations.size();
//                if(numPotentialDestinations > 0){
//
//                    Region randomDestination = potentialDestinations.get(random.nextInt(numPotentialDestinations));
//
//                    Order randomOrder;
//                    if(randomDestination.equals(randomUnit)){
//                        randomOrder = new HLDOrder(power, randomUnit);
//                    }else{
//                        randomOrder = new MTOOrder(power, randomUnit, randomDestination);
//                    }
//                    // Of course we could also propose random support orders, but we don't do that here.
//
//
//                    //We only generate deals for the current year and phase.
//                    // However, you can pick any year and phase here, as long as they do not lie in the past.
//                    // (actually, you can also propose deals for rounds in the past, but it doesn't make any sense
//                    //  since you obviously cannot obey such deals).
//                    randomOrderCommitments.add(new OrderCommitment(game.getYear(), game.getPhase(), randomOrder));
//                }
//
//            }
//
//            BasicDeal deal = new BasicDeal(randomOrderCommitments, demilitarizedZones);
//
//
//            return deal;
//        }



        /**
         * Each round, after each power has submitted its orders, this method is called several times:
         * once for each order submitted by any other power.
         *
         *
         * @param arg0 An order submitted by any of the other powers.
         */
    @Override
    public void receivedOrder(Order arg0) {
        // TODO Auto-generated method stub

    }

}
