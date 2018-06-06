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
import sorashido.DDAgent2.model.DipModel;
import sorashido.DDAgent2.negotiation.UtilityCalculator;
import sorashido.DDAgent2.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
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

    UtilityCalculator utilityCalculator;
    DipModel dipModel;

    public static void main(String[] args) {
        sorashido.DDAgent2.DDAgent2 myPlayer = new sorashido.DDAgent2.DDAgent2(args);
        myPlayer.run();
    }

    private DDAgent2(String[] args) {
        super(args);

        try {
            utilityCalculator = new UtilityCalculator();
            dipModel = new DipModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        for(Power power : game.getNonDeadPowers()){
            dipModel.updateThreshold(game.getYear(), me.getName(), power.getName(), power.getOwnedSCs().size());
        }

        while (System.currentTimeMillis() < negotiationDeadline) {
            while (hasMessage()) {
                manageProposedMessage();
            }
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
        dipModel.readCSV(game.getYear());

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
            this.getLogger().logln("DDAgent2.negotiate() Propoced acceptance from " + receivedMessage.getSender() + ": " + receivedMessage.getContent(), printToConsole);
            proposedAction(receivedMessage);
        }
        //confirmed
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.CONFIRM)) {
            DiplomacyProposal confirmedProposal = (DiplomacyProposal) receivedMessage.getContent();
            this.getLogger().logln("DDAgent2.negotiate() confirmed acceptance from " + receivedMessage.getSender() + ": " + confirmedProposal, printToConsole);
            confirmedAction(confirmedProposal);
        }
        //rejected
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.REJECT)) {
            DiplomacyProposal rejectedProposal = (DiplomacyProposal) receivedMessage.getContent();
            this.getLogger().logln("DDAgent2.negotiate() rejected acceptance from " + receivedMessage.getSender() + ": " + rejectedProposal, printToConsole);
            rejectedAction(rejectedProposal);
        }
        else {
            //We have received any other kind of message.
            this.getLogger().logln("Received a message of unhandled type: " + receivedMessage.getPerformative() + ". Message content: " + receivedMessage.getContent().toString(), printToConsole);
        }
    }

    private void proposeMessage() {
        // 敵対関数から閾値を求める, 敵対度が高いほど
        for(Power power : this.getNegotiatingPowers()){
            Double threshold  = dipModel.getThreshold(power.getName());
            List<BasicDeal> newDealToProposes = searchForNewDealToPropose(power, threshold);

            // これまでの取引と矛盾するか調べる
            String consistencyReport = null;
            consistencyReport = Utilities.testConsistency(game, newDealToProposes);
            if(calcUtilityValue(newDealToProposes, power) > 0 && consistencyReport == null){
                for (BasicDeal newDealToPropose : newDealToProposes) {
                    consistencyReport = Utilities.testValidity(game, newDealToPropose);
                    if (newDealToPropose != null && consistencyReport==null) {
                        this.getLogger().logln("DDAgent2.negotiate() Proposing: " + newDealToPropose, printToConsole);
                        this.proposeDeal(newDealToPropose);
                    }
                }
            }
        }
    }

    /**
     * simulated annealing
     */
    static double START_TEMPERATURE = 1.0; // 開始温度
    static double END_TEMPERATURE = 0.001; // 終了温度
    static double COOL = 0.5; // 冷却度
    List<BasicDeal> searchForNewDealToPropose(Power opponent, Double threshold) {
        List<BasicDeal> deals = new ArrayList<BasicDeal>();

        List<BasicDeal> commitments = this.getConfirmedDeals(); //現在の取り決め
        if (calcPlanValue(commitments, opponent) == -1.0) { //取り決めのために行動できない -> 交渉する必要なし
            return null;
        }

        ArrayList<Power> powers = new ArrayList<Power>(2);
        powers.add(me);
        powers.add(opponent);

        // baseList
        List<OrderCommitment> baseLists = new ArrayList<>();
        for(Region unit: me.getControlledRegions()){
            OrderCommitment orderDeal = generateOrderDeal(unit);
            if(orderDeal != null && random.nextDouble() < 0.3){
                baseLists.add(orderDeal);
            }
        }
        for(Region unit: opponent.getControlledRegions()){
            OrderCommitment orderDeal = generateOrderDeal(unit);
            if(orderDeal != null && random.nextDouble() < 0.3){
                baseLists.add(orderDeal);
            }
        }

        List<DMZ> baseDmzs = generateMyDMZ(opponent);

        BasicDeal currentDeal = new BasicDeal(baseLists, baseDmzs);
        double currenDealUtil = calcUtilityValue(currentDeal, opponent);
//        double targetDealUtil = 0.0;
//
//        List<BasicDeal> targetDeal = new ArrayList<>(); // 最適効用値BidのArrayList

        double currentTemperature = START_TEMPERATURE;
        double newcost = 2.0;
        double currentCost = 2.0;
        double p;//
        while(currentTemperature > END_TEMPERATURE){
            BasicDeal nextDeal = currentDeal;
            List<OrderCommitment> orderCommitment = nextDeal.getOrderCommitments();
            List<DMZ> dmzs = nextDeal.getDemilitarizedZones();
            Double r = random.nextDouble(); //random.nextInt(8);
            if((r < 0.25) && me.getControlledRegions().size() > 0){
                // 0. basicListに自分のものを入れる
                Region region = me.getControlledRegions().get(random.nextInt(me.getControlledRegions().size()));
                OrderCommitment orderDeal = generateOrderDeal(region);
                if(orderDeal != null && !orderCommitment.contains(orderDeal)){
                    if(orderCommitment.size() > 0) orderCommitment.remove(random.nextInt(orderCommitment.size()));
                    orderCommitment.add(orderDeal);
                }
            }
            else if((r < 0.70) && opponent.getControlledRegions().size() > 0) {
                // 1. basicListに相手のものを入れる
                Region region = opponent.getControlledRegions().get(random.nextInt(opponent.getControlledRegions().size()));
                OrderCommitment orderDeal = generateOrderDeal(region);
                if(orderDeal != null && !orderCommitment.contains(orderDeal)){
                    if(orderCommitment.size() > 0) orderCommitment.remove(random.nextInt(orderCommitment.size()));
                    orderCommitment.add(orderDeal);
                }
            }
            else if(r < 0.85 && me.getControlledRegions().size() > 0){
                // 2 basicDmzsに自分のものを入れる
                Region region = me.getControlledRegions().get(random.nextInt(me.getControlledRegions().size()));
                List<Province> units = new ArrayList<>();
                units.add(region.getProvince());
                DMZ dmz = new DMZ(game.getYear(), game.getPhase(), powers, units);
                if(dmzs.size() > 0) dmzs.remove(random.nextInt(dmzs.size()));
                if(!dmzs.contains(dmz)) dmzs.add(dmz);
            }
            else if(r < 1.0 && opponent.getControlledRegions().size() > 0) {
                // 3. basicDmzsに相手のものを入れる
                Region region = opponent.getControlledRegions().get(random.nextInt(opponent.getControlledRegions().size()));
                List<Province> units = new ArrayList<>();
                units.add(region.getProvince());
                DMZ dmz = new DMZ(game.getYear(), game.getPhase(), powers, units);
                if(dmzs.size() > 0) dmzs.remove(random.nextInt(dmzs.size()));
                if(!dmzs.contains(dmz)) dmzs.add(dmz);
            }
            if (dmzs.isEmpty()) dmzs = new ArrayList<>(3);
            nextDeal = new BasicDeal(orderCommitment, dmzs);
            Double nextDealUtil = calcUtilityValue(nextDeal, opponent);
            newcost = nextDealUtil;
            currentCost = currenDealUtil;
//            newcost = Math.abs(threshold - nextDealUtil);
//            currentCost = Math.abs(threshold - currenDealUtil);
            p = Math.exp(-Math.abs(newcost - currentCost) / currentTemperature);
            if (newcost > currentCost || p > random.nextDouble()) {
                currentDeal = nextDeal;
                currenDealUtil = nextDealUtil;
            }
//            System.out.println(currenDealUtil);

            // 更新
//            if(currenDealUtil >= threshold){
//                if(targetDeal.size() == 0){
//                    targetDeal.add(currentDeal);
//                    targetDealUtil = currenDealUtil;
//                }else {
//                    if (currenDealUtil < targetDealUtil) {
//                        targetDeal.clear();
//                        targetDeal.add(currentDeal);
//                        targetDealUtil = currenDealUtil;
//                    } else if (currenDealUtil == targetDealUtil) {
//                        targetDeal.add(currentDeal);
//                    }
//                }
//            }
            currentTemperature = currentTemperature * COOL; // 温度を下げる
        }
        deals.add(currentDeal);
//        if (targetDeal.size() == 0){ deals.add(currentDeal);}
//        else deals.add(targetDeal.get(random.nextInt(targetDeal.size())));
        return deals;
    }

    /**
     * search deal method
     */
    private OrderCommitment generateOrderDeal(Region unit){
        Power power = game.getController(unit);

        ArrayList<OrderCommitment> carray = new ArrayList<>();
        ArrayList<Double> darray = new ArrayList<>();

        //unitの移動可能なところ
        List<Region> adjacentRegions = new ArrayList<>(unit.getAdjacentRegions());
        adjacentRegions.add(unit);

        OrderCommitment maxOrderCommitment = null;
        Double maxValue = 0.0;

        for(Region adjascentRegion : adjacentRegions) {
            Order order;
            if (adjascentRegion.equals(unit)) {
                order = new HLDOrder(power, unit);
            } else {
                order = new MTOOrder(power, unit, adjascentRegion);
                for (HLDOrder myHLDOrder : myHLDOrders) {
                    if (myHLDOrder.getLocation().equals(adjascentRegion)) {
                        order = new SUPOrder(power, unit, myHLDOrder);
                    }
                }
                for (MTOOrder myMTOOrder : myMTOrders) {
                    if (myMTOOrder.getDestination().equals(adjascentRegion)) {
                        order = new SUPMTOOrder(power, unit, myMTOOrder);
                    }
                }
            }
            OrderCommitment commitment = new OrderCommitment(game.getYear(), game.getPhase(), order);
            double value = calcPlanValue(commitment, me);
            if (value > maxValue && random.nextDouble() > 0.1){
                maxOrderCommitment = commitment;
                maxValue = value;
            }
        }
        return maxOrderCommitment;
    }

    private List<DMZ> generateMyDMZ(Power opponent){
        List<DMZ> dmzs = new ArrayList<>();

        ArrayList<Power> powers = new ArrayList<>();
        powers.add(me);
        powers.add(opponent);

        Region region = me.getControlledRegions().get(random.nextInt(me.getControlledRegions().size()));
        List<Province> units = new ArrayList<>();
        units.add(region.getProvince());
        DMZ dmz = new DMZ(game.getYear(), game.getPhase(), powers, units);
        dmzs.add(dmz);

        region = opponent.getControlledRegions().get(random.nextInt(opponent.getControlledRegions().size()));
        units = new ArrayList<>();
        units.add(region.getProvince());
        dmz = new DMZ(game.getYear(), game.getPhase(), powers, units);
        dmzs.add(dmz);

        return dmzs;
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
                if(calcUtilityValue(commitments, power) > 0){
                    this.acceptProposal(receivedProposal.getId());
                    this.getLogger().logln("DDAgent2.negotiate()  Accepting: " + receivedProposal, printToConsole);
                }
            }
        }
    }

    private void rejectedAction(DiplomacyProposal rejectedProposal) {

    }

    /**
     *  Utility Calculator
     */
    private Double calcUtilityValue(List<BasicDeal> commitments, Power opponents){
        Double threshold = dipModel.getThreshold(opponents.getName());
        return (calcPlanValue(commitments, me) + threshold*calcPlanValue(commitments, opponents));
    }

    private Double calcUtilityValue(BasicDeal basicDeal, Power opponents){
        return (calcPlanValue(basicDeal, me) + calcPlanValue(basicDeal, opponents));
    }

    private Double calcPlanValue(List<BasicDeal> commitments, Power power){
        Plan plan = this.dBraneTactics.determineBestPlan(game, power, commitments);
        if (plan == null) {
            return -1.0;
        }

        int state = 0;
        for (Power p :game.getNonDeadPowers()){
            int n = 0;
            if(p.getOwnedSCs().size()>6){
                n = 1;
            }
            if(p.getName().equals("ENG")){  state += (64 * n); }
            else if(p.getName().equals("FRA")){ state += (32 * n); }
            else if(p.getName().equals("ITA")){ state += (16 * n); }
            else if(p.getName().equals("RUS")){ state += (8 * n); }
            else if(p.getName().equals("TUR")){ state += (4 * n); }
            else if(p.getName().equals("GER")){ state += (2 * n); }
            else if(p.getName().equals("AUS")){ state += n; }
        }

        HashMap<String, Integer> util = utilityCalculator.getwinlocation(game.getYear(), game.getPhase().name(), power.getName(), state);
        Integer sum = util.values().stream().mapToInt(Integer::intValue).sum();
        double utilvalue = 0;
        for (Order order : plan.getMyOrders()){
            String region = order.getLocation().toString().substring(0, 3);
            if (util.keySet().contains(region)){
                utilvalue += ((double)util.get(region)/(double)sum);
            }
        }
        return  utilvalue + (double)plan.getValue(); //myPlan.getValue();
    }

    private Double calcPlanValue(BasicDeal basicDeal, Power power){
        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(basicDeal);
        return calcPlanValue(commitments, power);
    }

    private Double calcPlanValue(DMZ commitment, Power power){

        List<OrderCommitment> orderCommitments = new ArrayList<>();
        List<DMZ> demilitarizedZones = new ArrayList<>(3);
        demilitarizedZones.add(commitment);
        BasicDeal deal = new BasicDeal(orderCommitments, demilitarizedZones);

        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(deal);

        return calcPlanValue(commitments, power);
    }

    private Double calcPlanValue(OrderCommitment commitment, Power power){

        List<OrderCommitment> orderCommitments = new ArrayList<>();
        orderCommitments.add(commitment);
        List<DMZ> demilitarizedZones = new ArrayList<>(3);
        BasicDeal deal = new BasicDeal(orderCommitments, demilitarizedZones);

        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(deal);

        return calcPlanValue(commitments, power);
   }
}
