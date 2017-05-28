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
        Map<String, List<BasicDeal>> newDealToProposes = null; //各国に対する提案候補
        List<Order> Orders = null;//

//        Map<String, Pair<Integer,Integer>> powerParam = null;
//        Pair pair = new Pair(0.6, 0.4);

        //me.getName()

        //交渉の間, ループし続ける
        while(System.currentTimeMillis() < negotiationDeadline){
//          double restTime = (negotiationDeadline - System.currentTimeMillis())/1000; //残り時間s
//          myParam, opParamの値を変えることで閾値の制御

            //1. 自分が相手に提案
            //1.1 各国それぞれへの提案の候補を個別に探索 (自分と相手の効用値に基づいて探索, (効用値は線形に妥協しても良い = \alpha \betaの値))
            for(Power power :this.getNegotiatingPowers()) {
                if(this.getNegotiatingPowers().size() < 2){
                    break;
                }
                List<BasicDeal> newDealToPropose = searchForNewDealToPropose(power, 0.65, 0.35);

                if(!newDealToPropose.isEmpty()){
                    newDealToProposes.put(power.getName(), newDealToPropose);
                }
            }

            //1.2 国ごとの交渉に矛盾がない組み合わせを自らの利益で最適化!!




            //2. 送られてきたメッセージを処理
            while(hasMessage()){
                //2.1 効用値を計算し自身の利益がある一定以上であれば許可


            }


            //3. 引き分けを提案
        }
    }

    List<BasicDeal> searchForNewDealToPropose(Power opponent, double myParam, double opParam) {

        List<OrderCommitment> goodOrderCommitments = null;

        List<BasicDeal> commitments = this.getConfirmedDeals(); //現在の取り決め
        Double baseLine = calcPlanValue(commitments, opponent, myParam, opParam);
        if (baseLine == null) { //取り決めのために行動できない -> 交渉する必要なし
            return null;
        }

        //army毎に計算 効用値が最も高くなるものを追加
        List<Region> unitsOfOpponent = opponent.getControlledRegions();
        for(Region unit: unitsOfOpponent){
            OrderCommitment goodOrder = generateOrderDeal(unit, baseLine, myParam, opParam);
            if(goodOrder != null){
                goodOrderCommitments.add(goodOrder);
            }
        }

        //opponent毎にどんな不可侵条約を結びたいかを計算
        List<DMZ> goodDMZDeals = generateDMZ(opponent, myParam, opParam);

//      OrderCommitmentのリスト, DMZのリストから組み合わせを最適化しdealとする(矛盾するものを取り除く)
//      解の候補を

        List<BasicDeal> goodDeals = null;
        BasicDeal deal = new BasicDeal(goodOrderCommitments, goodDMZDeals);
        goodDeals.add(deal);
//		List<OrderCommitment> randomOrderCommitments = new ArrayList<OrderCommitment>();

        return goodDeals;
    }

    //unit がどう動くのが最も良いのかを探索 (なにもない場合と変わらない場合はnullを返す)
    private OrderCommitment generateOrderDeal(Region unit, double baseLine, double myParam, double opParam){
        OrderCommitment goodOrder = null;

        Power power = game.getController(unit);

        //unitの移動可能なところ
        List<Region> adjacentRegions = new ArrayList<>(unit.getAdjacentRegions());
        adjacentRegions.add(unit);
        //

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

    private List<DMZ> generateDMZ(Power power, double myParam, double opParam){
        List<DMZ> goodDMZs = null;

//        double value = calcPlanValue();
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
