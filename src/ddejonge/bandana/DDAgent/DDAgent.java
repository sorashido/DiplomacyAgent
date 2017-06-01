package ddejonge.bandana.DDAgent;

import com.sun.tools.javac.util.Pair;
import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.exampleAgents.ANACExampleNegotiator;
import ddejonge.bandana.negoProtocol.*;
import ddejonge.bandana.tools.Utilities;
import ddejonge.bandana.tournament.TournamentRunner;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.HLDOrder;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

//    public Random random = new Random();
    Boolean print = true; //printするか否か
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

        boolean printToConsole = false; //if set to true the text will be written to file, as well as printed to the standard output stream. If set to false it will only be written to file.
        this.getLogger().logln("game is starting!", printToConsole);
    }


    @Override
    public void negotiate(long negotiationDeadline) {
//        Map<String, List<BasicDeal>> newDealToProposes = null; //各国に対する提案候補
//        List<Order> Orders = null;//

        //交渉の間, ループし続ける
        while(System.currentTimeMillis() < negotiationDeadline){
//          double restTime = (negotiationDeadline - System.currentTimeMillis())/1000; //残り時間s
//          myParam, opParamの値を変えることで閾値の制御

            //1. 送られてきたメッセージを処理
            while(hasMessage()){
                manageMessage();
            }

            //2. 自分が相手に提案
            //2.1 各国それぞれへの提案の候補を個別に探索 (自分と相手の効用値に基づいて探索, (効用値は線形に妥協しても良いかも = \alpha \betaの値))
            // \alpha + \beta の値を線形に落としていく感じ
            if(this.getNegotiatingPowers().size() < 2){
                break;
            }
            for(Power power :this.getNegotiatingPowers()){
                if(!power.equals(me)) {
                    BasicDeal newDealToPropose = searchForNewDealToPropose(power, 0.65, 0.35);

                    // これまでの取引と矛盾するか調べる

                    if (newDealToPropose != null) {
                        this.getLogger().logln("DDBrane.negotiate() Proposing: " + newDealToPropose, print);
                        this.proposeDeal(newDealToPropose);
                    }
                }
            }

            //3. 引き分けを提案
        }
    }

    void manageMessage(){
        Message receivedMessage = removeMessageFromQueue();

        //accept
        if(receivedMessage.getPerformative().equals(DiplomacyNegoClient.ACCEPT)){
            DiplomacyProposal acceptedProposal = (DiplomacyProposal)receivedMessage.getContent();
            this.getLogger().logln("DDBrane.negotiate() Received acceptance from " + receivedMessage.getSender() + ": " + acceptedProposal, print);
        }//propose
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
                List<BasicDeal> commitments = new ArrayList<BasicDeal>();
                commitments.addAll(this.getConfirmedDeals());
                commitments.add(deal);
                consistencyReport = Utilities.testConsistency(game, commitments);

                if(consistencyReport == null){
                    //TODO:相手との相関を求め考慮する
                    if(this.dBraneTactics.determineBestPlan(game, me, commitments).getValue() > 0){
                        this.acceptProposal(receivedProposal.getId());
                        this.getLogger().logln("DDBrane.negotiate()  Accepting: " + receivedProposal, print);
                    }
                }
            }
        }//confirm
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
        }//reject
        else if(receivedMessage.getPerformative().equals(DiplomacyNegoClient.REJECT)){

            DiplomacyProposal rejectedProposal = (DiplomacyProposal)receivedMessage.getContent();
        }else{
            //We have received any other kind of message.
            this.getLogger().logln("Received a message of unhandled type: " + receivedMessage.getPerformative() + ". Message content: " + receivedMessage.getContent().toString(), print);
        }
    }

    BasicDeal searchForNewDealToPropose(Power opponent, double myParam, double opParam) {
        //提案
        BasicDeal goodDeal = null;

        List<OrderCommitment> goodOrderCommitments = new ArrayList<OrderCommitment>();;
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
        //goodDMZDeals = generateDMZ(opponent, baseLine ,myParam, opParam);

//      OrderCommitmentのリストとDMZのリストから組み合わせを最適化しdealとする(矛盾するものを取り除く)
        if(!goodOrderCommitments.isEmpty() || !goodDMZDeals.isEmpty()){
            goodDeal = new BasicDeal(goodOrderCommitments, goodDMZDeals);
        }
        return goodDeal;
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
            }
            OrderCommitment commitment = new OrderCommitment(game.getYear(), game.getPhase(), order);
            double value = calcPlanValue(commitment, power, myParam, opParam);
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

    private Double calcPlanValue(List<BasicDeal> commitments, Power opponent, double myParam, double opParam){
        Plan myPlan = this.dBraneTactics.determineBestPlan(game, me, commitments);
        Plan opPlan = this.dBraneTactics.determineBestPlan(game, opponent, commitments);

        if (myPlan == null || opPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
            return null;
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
