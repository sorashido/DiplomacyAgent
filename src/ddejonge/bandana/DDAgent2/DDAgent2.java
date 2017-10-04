package ddejonge.bandana.DDAgent2;

import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import es.csic.iiia.fabregues.dip.orders.*;

//ゲームが始まるときに自分の評価値をgetする!
//1. 土地のログを作る (その年にunitがいる土地を保存):自分の国：相手の国：すべて
//2. 勝ったときには, + 負けたときには-で土地の評価を作る(すべての国に対して):
//
//3. 盤の評価を評価値の計算に入れる:
// 相手が取って良い土地, 駄目な土地がわかる！！
//4. その情報に基づいて, 評価値を計算する
// ゲームが終わるたびにログを更新する!:
//
public class DDAgent2 extends ANACNegotiator {

    public static void main(String[] args){
        ddejonge.bandana.DDAgent2.DDAgent2 myPlayer = new ddejonge.bandana.DDAgent2.DDAgent2(args);

        myPlayer.run();
    }

    final Boolean print = false; //printするか否か
    DBraneTactics dBraneTactics = new DBraneTactics();

    //Constructor
    /**
     * You must implement a Constructor with exactly this signature.
     * The body of the Constructor must start with the line <code>super(args)</code>
     * but below that line you can put whatever you like.
     * @param args
     */
    public DDAgent2(String[] args) {
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
