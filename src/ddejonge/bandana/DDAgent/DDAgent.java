package ddejonge.bandana.DDAgent;

import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import es.csic.iiia.fabregues.dip.orders.Order;

/**
 * Created by tela on 2017/05/09.
 */
public class DDAgent extends ANACNegotiator{


    public static void main(String[] args){
        ANACNegotiator myPlayer = new DDAgent(args);
        myPlayer.run();
    }

    DBraneTactics dBraneTactics;
    //Constructor
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

    }

    /**
     * Each round, after each power has submitted its orders, this method is called several times:
     * once for each order submitted by any other power.
     *
     *
     * @param order An order submitted by any of the other powers.
     */
    @Override
    public void receivedOrder(Order order) {

    }
}
