package sorashido.DDAgent2;

import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.*;
import ddejonge.bandana.tools.Utilities;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.*;
import sorashido.DDAgent2.negotiation.UtilityCalculator;
import sorashido.DDAgent2.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DDAgent2 extends ANACNegotiator {

    final boolean printToConsole = false;
    final Constants constants = new Constants();

    DBraneTactics dBraneTactics = new DBraneTactics();
    private Random random = new Random();

    List<MTOOrder> myMTOrders = new ArrayList<>();
    List<HLDOrder> myHLDOrders = new ArrayList<>();

    List<MTOOrder> opMTOrders = new ArrayList<>();
    List<HLDOrder> opHLDOrders = new ArrayList<>();

    public static void main(String[] args) {
//        sorashido.DDAgent2.DDAgent2 myPlayer = new sorashido.DDAgent2.DDAgent2(args);
//        myPlayer.run();
        try {
            UtilityCalculator utilityCalculator = new UtilityCalculator();
            System.out.println(utilityCalculator.getlocation("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DDAgent2(String[] args) {
        super(args);

        dBraneTactics = this.getTacticalModule();
    }

    /**
     *  override method
     */
    @Override
    public void start() {
        this.getLogger().logln("game is starting!", printToConsole);
    }

    @Override
    public void negotiate(long negotiationDeadline) {

        initNegotiate();

        while (System.currentTimeMillis() < negotiationDeadline) {
            while (hasMessage()) {
                manageProposedMessage();
            }
            //
            proposeMessage();
        }
    }

    @Override
    public void receivedOrder(Order arg0) {
        if (game.getYear() == 1920 && game.getPhase().equals("WIN")) {
        }
    }

    /**
     * init method
     */
    private void initNegotiate() {
        myMTOrders = new ArrayList<>();
        myHLDOrders = new ArrayList<>();

        opMTOrders = new ArrayList<>();
        opHLDOrders = new ArrayList<>();

        for (Power power : game.getNonDeadPowers()) {
            for (Region unit : power.getControlledRegions()) {
                List<Region> adjacentRegions = new ArrayList<>(unit.getAdjacentRegions());
                adjacentRegions.add(unit);

                for (Region adjascentRegion : adjacentRegions) {
                    if (adjascentRegion.equals(unit)) {
                        if (power.getName().equals(me.getName())) myHLDOrders.add(new HLDOrder(power, unit));
                        else opHLDOrders.add(new HLDOrder(power, unit));
                    } else {
                        if (power.getName().equals(me.getName())) myMTOrders.add(new MTOOrder(power, unit, adjascentRegion));
                        else opMTOrders.add(new MTOOrder(power, unit, adjascentRegion));
                    }
                }
            }
        }
    }

    /**
     *  negotiation
     */
    private void manageProposedMessage() {
        Message receivedMessage = removeMessageFromQueue();
        //accepted
        if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.ACCEPT)) {
            DiplomacyProposal acceptedProposal = (DiplomacyProposal) receivedMessage.getContent();
            this.getLogger().logln("DDAgent2.negotiate() Received acceptance from " + receivedMessage.getSender() + ": " + acceptedProposal, printToConsole);
            acceptedAction(acceptedProposal);
        }
        //proposed
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.PROPOSE)) {
            proposedAction(receivedMessage);
        }
        //confirmed
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.CONFIRM)) {
            DiplomacyProposal confirmedProposal = (DiplomacyProposal) receivedMessage.getContent();
            confirmedAction(confirmedProposal);
        }
        //rejected
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.REJECT)) {
            DiplomacyProposal rejectedProposal = (DiplomacyProposal) receivedMessage.getContent();
            rejectedAction(rejectedProposal);
        }
        else {
            //We have received any other kind of message.
            this.getLogger().logln("Received a message of unhandled type: " + receivedMessage.getPerformative() + ". Message content: " + receivedMessage.getContent().toString(), printToConsole);
        }
    }

    private void proposeMessage() {
        final double eps = 0.1;
        if (random.nextDouble() < eps) {

        } else {

        }
    }

    /**
     * search deal method
     */
    private OrderCommitment generateOrderDeal(Region unit, double baseLine){
        Power power = game.getController(unit);

        //unitの移動可能なところ
        List<Region> adjacentRegions = new ArrayList<>(unit.getAdjacentRegions());
        adjacentRegions.add(unit);

        OrderCommitment maxOrderCommitment = null;
        Double maxValue = 0.0;

        for(Region adjascentRegion : adjacentRegions){
            Order order;
            if(adjascentRegion.equals(unit)){
                order = new HLDOrder(power, unit);
            }else{
                order = new MTOOrder(power, unit, adjascentRegion);
                for(HLDOrder myHLDOrder : myHLDOrders){
                    if(myHLDOrder.getLocation().equals(adjascentRegion)){
                        order = new SUPOrder(power, unit, myHLDOrder);
                    }
                }
                for(MTOOrder myMTOOrder : myMTOrders){
                    if(myMTOOrder.getDestination().equals(adjascentRegion)){
                        order = new SUPMTOOrder(power, unit, myMTOOrder);
                    }
                }
            }
            OrderCommitment commitment = new OrderCommitment(game.getYear(), game.getPhase(), order);
            double value = calcPlanValue(commitment, me);
            if(value > baseLine && value > maxValue){
                maxValue = value;
                maxOrderCommitment = commitment;
            }
            commitment = new OrderCommitment(game.getYear(), game.getPhase(), order);
            value = calcPlanValue(commitment, me);
            if(value > baseLine && value > maxValue){
                maxValue = value;
                maxOrderCommitment = commitment;
            }
        }

        //baseLineと同じであればnull
        return maxOrderCommitment;
    }

    private List<DMZ> generateDMZ(Power opponent){
        List<DMZ> goodDMZs = new ArrayList<DMZ>(3);

        ArrayList<Power> powers = new ArrayList<Power>(2);
        powers.add(opponent);

        //自分が移動可能なところ + 自分が征服したところ
        List<Region> unitsOfOpponent = me.getControlledRegions();
        for(Region unit: unitsOfOpponent){
            unitsOfOpponent.addAll(unit.getAdjacentRegions());
        }

        ArrayList<Province> provinces = new ArrayList<Province>();
        for(Region region: unitsOfOpponent){
            DMZ dmz = new DMZ(game.getYear(), game.getPhase(), powers, (List<Province>) region.getProvince());
            if(calcPlanValue(dmz, me) > 0.5){
                goodDMZs.add(dmz);
            }
        }
        //This agent only generates deals for the current year and phase.
        // However, you can pick any year and phase here, as long as they do not lie in the past.
        // (actually, you can also propose deals for rounds in the past, but it doesn't make any sense
        //  since you obviously cannot obey such deals).
        return goodDMZs;
    }



    /**
     *  received message method
     */
    private void confirmedAction(DiplomacyProposal confirmedProposal){
        BasicDeal confirmedDeal = (BasicDeal)confirmedProposal.getProposedDeal();
        List<BasicDeal> deals = new ArrayList<>(2);
        deals.add(confirmedDeal);
        for(DiplomacyProposal standingProposal : this.getUnconfirmedProposals()){
            deals.add((BasicDeal)standingProposal.getProposedDeal());
            if(Utilities.testConsistency(game, deals) != null){
                this.rejectProposal(standingProposal.getId());
            }
            deals.remove(1);
        }
    }

    private void acceptedAction(DiplomacyProposal acceptedProposal){

    }

    private void proposedAction(Message receivedMessage){
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        BasicDeal deal = (BasicDeal)receivedProposal.getProposedDeal();
        boolean outDated = false;
        for(DMZ dmz : deal.getDemilitarizedZones()){
            if( isHistory(dmz.getPhase(), dmz.getYear())){
                outDated = true;
                break;
            }
        }
        for(OrderCommitment orderCommitment : deal.getOrderCommitments()){
            if( isHistory(orderCommitment.getPhase(), orderCommitment.getYear())){
                outDated = true;
                break;
            }
        }
        //If the deal is not outdated, then check that it is consistent with the deals we are already committed to.
        String consistencyReport = null;
        if(!outDated){
            double numOwned = 0;
            for(Power power: this.getNegotiatingPowers()){
                numOwned += power.getOwnedSCs().size();
            }

            List<BasicDeal> commitments = new ArrayList<BasicDeal>();
            commitments.addAll(this.getConfirmedDeals());
            commitments.add(deal);
            consistencyReport = Utilities.testConsistency(game, commitments);

            if(consistencyReport == null){
                Power power = game.getPower(receivedMessage.getSender());

                if(calcPlanValue(commitments, power) > 0.8){
                    this.acceptProposal(receivedProposal.getId());
                    this.getLogger().logln("DDBrane.negotiate()  Accepting: " + receivedProposal, printToConsole);
                }
            }
        }
    }

    private void rejectedAction(DiplomacyProposal rejectedProposal) {

    }

    /**
     *  Utility Calculator
     */
    private Integer calcPlanValue(List<BasicDeal> commitments, Power power){
        Plan myPlan = this.dBraneTactics.determineBestPlan(game, power, commitments);
        if (myPlan == null) {
            return -1;
        }
        return  myPlan.getValue();
    }

    private Integer calcPlanValue(DMZ commitment, Power power){

        List<OrderCommitment> orderCommitments = new ArrayList<>();
        List<DMZ> demilitarizedZones = new ArrayList<>(3);
        demilitarizedZones.add(commitment);
        BasicDeal deal = new BasicDeal(orderCommitments, demilitarizedZones);

        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(deal);

        return calcPlanValue(commitments, power);
    }

    private Integer calcPlanValue(OrderCommitment commitment, Power power){

        List<OrderCommitment> orderCommitments = new ArrayList<>();
        orderCommitments.add(commitment);
        List<DMZ> demilitarizedZones = new ArrayList<>(3);
        BasicDeal deal = new BasicDeal(orderCommitments, demilitarizedZones);

        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(deal);

        return calcPlanValue(commitments, power);
   }
}
