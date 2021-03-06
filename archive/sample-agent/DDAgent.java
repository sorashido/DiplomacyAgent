package ddejonge.bandana.DDAgent;

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

import java.util.*;

//ゲームが始まるときに自分の評価値をgetする!
//1. 土地のログを作る (その年にunitがいる土地を保存):自分の国：相手の国：すべて
//2. 勝ったときには, + 負けたときには-で土地の評価を作る(すべての国に対して):
//
//3. 盤の評価を評価値の計算に入れる:
// 相手が取って良い土地, 駄目な土地がわかる！！
//4. その情報に基づいて, 評価値を計算する
// ゲームが終わるたびにログを更新する!:
//

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

    final Boolean print = false; //printするか否か

    DBraneTactics dBraneTactics = new DBraneTactics();
    Parameters parameters = new Parameters();
//    public Random random = new Random();

    //    Map<String, HashMap<String, Double>> piasonMap = new HashMap<>();
    Map<String, HashMap<Integer, Double>> relationParams = new HashMap<>();
    List<BasicDeal> rejectedproposals;

    List<MTOOrder> myMTOrders = new ArrayList<>(); //自分が移動可能なところ
    List<HLDOrder> myHLDOrders = new ArrayList<>(); //自分が移動可能なところ

    //土地の評価のために, unitが止まっていた場所を数える
    Map<String, Integer> numUnit = new HashMap<>();

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
        relationParams = parameters.getRelationParams();

        boolean printToConsole = false; //if set to true the text will be written to file, as well as printed to the standard output stream. If set to false it will only be written to file.
        this.getLogger().logln("game is starting!", printToConsole);
    }


    @Override
    public void negotiate(long negotiationDeadline) {
//        Map<String, List<BasicDeal>> newDealToProposes = null; //各国に対する提案候補
//        List<Order> Orders = null;//

        myMTOrders = new ArrayList<>(); //自分が移動可能なところ
        myHLDOrders = new ArrayList<>(); //自分がいるところ
        for (Region unit: me.getControlledRegions()){
            //unitの移動可能なところ
            List<Region> adjacentRegions = new ArrayList<>(unit.getAdjacentRegions());
            adjacentRegions.add(unit);

            for(Region adjascentRegion : adjacentRegions) {
                if (adjascentRegion.equals(unit)) {
                    myHLDOrders.add(new HLDOrder(me, unit));
                } else {
                    myMTOrders.add(new MTOOrder(me, unit, adjascentRegion));
                }
            }
        }

        //占領している地域を保存する
        for(Power power: this.getNegotiatingPowers()) {
            for (Region unit : power.getControlledRegions()) {
                if (numUnit.keySet().contains(power.getName()+unit.getName())) {
                    numUnit.put(power.getName()+unit.getName(), numUnit.get(unit.getName()) + 1);
                } else {
                    numUnit.put(unit.getName(), 1);
                }
            }
        }

        //交渉の間, ループし続ける
        while(System.currentTimeMillis() < negotiationDeadline){
//          double restTime = (negotiationDeadline - System.currentTimeMillis())/1000; //残り時間s
//          myParam, opParamの値を変えることで閾値の制御

            //1. 送られてきたメッセージを処理
            while(hasMessage()){
                manageMessage();
            }

            //2. 自分が相手に提案
            if(this.getNegotiatingPowers().size() < 2){
                break;
            }
            double numOwned = 0;
            for(Power power: this.getNegotiatingPowers()){
                numOwned += power.getOwnedSCs().size();
            }
            for(Power power :this.getNegotiatingPowers()){
                if(!power.equals(me)) {
                    Double relation  = relationParams.get(me.getName()+power.getName()).get(game.getYear());
                    Double myParam = 0.75 - 0.25 * relation;
                    Double opParam = 0.5 * relation - 0.5 * power.getOwnedSCs().size()/numOwned;

                    List<BasicDeal> newDealToProposes = searchForNewDealToPropose(power,myParam, opParam);

                    if(calcPlanValue(newDealToProposes, power, myParam, opParam) > 1.0) {
                        // これまでの取引と矛盾するか調べる
                        for (BasicDeal newDealToPropose : newDealToProposes) {
                            if (newDealToPropose != null) {
                                this.getLogger().logln("DDBrane.negotiate() Proposing: " + newDealToPropose, print);
                                this.proposeDeal(newDealToPropose);
                            }
                        }
                    }
                }
            }
            //3. 引き分けを提案
        }
    }

    /*メッセージを処理*/
    void manageMessage(){
        Message receivedMessage = removeMessageFromQueue();

        //accepted
        if(receivedMessage.getPerformative().equals(DiplomacyNegoClient.ACCEPT)){
            DiplomacyProposal acceptedProposal = (DiplomacyProposal)receivedMessage.getContent();
            this.getLogger().logln("DDBrane.negotiate() Received acceptance from " + receivedMessage.getSender() + ": " + acceptedProposal, print);
        }//proposed
        else if(receivedMessage.getPerformative().equals(DiplomacyNegoClient.PROPOSE)){
            DiplomacyProposal receivedProposal = (DiplomacyProposal)receivedMessage.getContent();
            this.getLogger().logln("DDBrane.negotiate() Received proposal: " + receivedProposal, print);
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
//                    Double relation = piasonMap.get(me.getName()).get(power.getName());
                    Double relation  = relationParams.get(me.getName()+power.getName()).get(game.getYear());
                    Double myParam = 0.75 - 0.25 * relation;
                    Double opParam = 0.5 * relation - 0.5 * power.getOwnedSCs().size()/numOwned;
                    if(calcPlanValue(commitments, power, myParam, opParam) > 1.0){
                        this.acceptProposal(receivedProposal.getId());
                        this.getLogger().logln("DDBrane.negotiate()  Accepting: " + receivedProposal, print);
                    }
                }
            }
        }//confirmed
        else if(receivedMessage.getPerformative().equals(DiplomacyNegoClient.CONFIRM)){

            // The protocol manager confirms that a certain proposal has been accepted by all players involved in it.
            // From now on we consider the deal as a binding agreement.

            DiplomacyProposal confirmedProposal = (DiplomacyProposal)receivedMessage.getContent();

            this.getLogger().logln("DDBrane.negotiate() RECEIVED CONFIRMATION OF: " + confirmedProposal, print);

            BasicDeal confirmedDeal = (BasicDeal)confirmedProposal.getProposedDeal();

            //Reject any proposal that has not yet been confirmed and that is inconsistent with the confirmed deal.
            // NOTE that normally this is not really necessary because the Notary will already check that
            // any deal is consistent with earlier confirmed deals before it becomes confirmed.
            List<BasicDeal> deals = new ArrayList<BasicDeal>(2);
            deals.add(confirmedDeal);
            for(DiplomacyProposal standingProposal : this.getUnconfirmedProposals()){

                //add this proposal to the list of deals.
                deals.add((BasicDeal)standingProposal.getProposedDeal());

                if(Utilities.testConsistency(game, deals) != null){
                    this.rejectProposal(standingProposal.getId());
                }

                //remove the deal again from the list, so that we can add the next standing deal to the list in the next iteration.
                deals.remove(1);
            }
        }//rejected
        else if(receivedMessage.getPerformative().equals(DiplomacyNegoClient.REJECT)){
            DiplomacyProposal rejectedProposal = (DiplomacyProposal)receivedMessage.getContent();
        }else{
            //We have received any other kind of message.
            this.getLogger().logln("Received a message of unhandled type: " + receivedMessage.getPerformative() + ". Message content: " + receivedMessage.getContent().toString(), print);
        }
    }


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
        if(game.getYear()==1920 && game.getPhase().equals("WIN")){
            System.out.println(numUnit);
        }
    }

    /*メッセージの探索*/
    List<BasicDeal> searchForNewDealToPropose(Power opponent, double myParam, double opParam) {
        //提案
        List<BasicDeal> goodDeals = new ArrayList<BasicDeal>();

        List<OrderCommitment> goodOrderCommitments = new ArrayList<OrderCommitment>();
        List<BasicDeal> commitments = this.getConfirmedDeals(); //現在の取り決め
        Double baseLine = calcPlanValue(commitments, opponent, myParam, opParam);
        if (baseLine == null) { //取り決めのために行動できない -> 交渉する必要なし
            return null;
        }

//      army毎に計算 効用値が最も高くなるものを追加
        List<Region> unitsOfOpponent = opponent.getControlledRegions();
        if(unitsOfOpponent.size()==0){
            return null;
        }
        for(Region unit: unitsOfOpponent){
            OrderCommitment goodOrder = generateOrderDeal(unit, baseLine, myParam, opParam);
            if(goodOrder != null){
                goodOrderCommitments.add(goodOrder);
            }
        }

//      opponent毎にどんな不可侵条約を結びたいかを計算(自分の行けるところのみを探索)
        List<DMZ> goodDMZDeals = new ArrayList<DMZ>();
//        goodDMZDeals = generateDMZ(opponent, baseLine ,myParam, opParam);
//        goodOrderCommitmentsの中から取り除くものを決める(ランダム)
//        if(goodOrderCommitments.size() > 3) {
//            goodOrderCommitments.remove(random.nextInt(goodOrderCommitments.size()));
//        }

        //commitment と dmzは別々のものとして提案
        if(!goodOrderCommitments.isEmpty()){
            goodDeals.add(new BasicDeal(goodOrderCommitments, new ArrayList<DMZ>()));
        }

        return goodDeals;
    }

    //unit がどう動くのが最も良いのかを探索 (なにもない場合と変わらない場合はnullを返す)
    private OrderCommitment generateOrderDeal(Region unit, double baseLine, double myParam, double opParam){
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
                for(HLDOrder myHLDOrder :myHLDOrders){
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
            double value = calcPlanValue(commitment, power, myParam, opParam);
            if(value > baseLine && value > maxValue){
                maxValue = value;
                maxOrderCommitment = commitment;
            }
            commitment = new OrderCommitment(game.getYear(), game.getPhase(), order);
            value = calcPlanValue(commitment, power, myParam, opParam);
            if(value > baseLine && value > maxValue){
                maxValue = value;
                maxOrderCommitment = commitment;
            }
        }

        //baseLineと同じであればnull
        return maxOrderCommitment;
    }

    private List<DMZ> generateDMZ(Power opponent, double baseLine, double myParam, double opParam){
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
            if(calcPlanValue(dmz, opponent, myParam, opParam) > baseLine){
                goodDMZs.add(dmz);
            }
        }
        //This agent only generates deals for the current year and phase.
        // However, you can pick any year and phase here, as long as they do not lie in the past.
        // (actually, you can also propose deals for rounds in the past, but it doesn't make any sense
        //  since you obviously cannot obey such deals).
//        goodDMZs.add(new DMZ( game.getYear(), game.getPhase(), (List<Power>) power, provinces));
        return goodDMZs;
    }

    /*評価関数の設計*/
    private Double calcPlanValue(List<BasicDeal> commitments, Power opponent, double myParam, double opParam){
        Plan myPlan = this.dBraneTactics.determineBestPlan(game, me, commitments);
        Plan opPlan = this.dBraneTactics.determineBestPlan(game, opponent, commitments);

        if (myPlan == null || opPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
            return 0.0;
        }
        return  (myPlan.getValue() * myParam + opPlan.getValue() * opParam);
    }

    private Double calcPlanValue(OrderCommitment commitment, Power opponent, double myParam, double opParam){

        List<OrderCommitment> orderCommitments = new ArrayList<OrderCommitment>();
        orderCommitments.add(commitment);
        List<DMZ> demilitarizedZones = new ArrayList<DMZ>(3);
        BasicDeal deal = new BasicDeal(orderCommitments, demilitarizedZones);

        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(deal);

        Plan myPlan = this.dBraneTactics.determineBestPlan(game, me, commitments);
        Plan opPlan = this.dBraneTactics.determineBestPlan(game, opponent, commitments);
        if (myPlan == null || opPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
            return 0.0;
        }
        return  (myPlan.getValue() * myParam + opPlan.getValue() * opParam);
    }

    private Double calcPlanValue(DMZ commitment, Power opponent, double myParam, double opParam){

        List<OrderCommitment> orderCommitments = new ArrayList<OrderCommitment>();
        List<DMZ> demilitarizedZones = new ArrayList<DMZ>(3);
        demilitarizedZones.add(commitment);
        BasicDeal deal = new BasicDeal(orderCommitments, demilitarizedZones);

        List<BasicDeal> commitments = this.getConfirmedDeals();
        commitments.add(deal);

        Plan myPlan = this.dBraneTactics.determineBestPlan(game, me, commitments);
        Plan opPlan = this.dBraneTactics.determineBestPlan(game, opponent, commitments);
        if (myPlan == null || opPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
            return 0.0;
        }
        return  (myPlan.getValue() * myParam + opPlan.getValue() * opParam);
    }
}
