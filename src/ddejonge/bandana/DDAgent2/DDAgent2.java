package ddejonge.bandana.DDAgent2;

import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import es.csic.iiia.fabregues.dip.orders.*;

public class DDAgent2 extends ANACNegotiator {

    final boolean printToConsole = false;
    DBraneTactics dBraneTactics = new DBraneTactics();

    public static void main(String[] args){
        ddejonge.bandana.DDAgent2.DDAgent2 myPlayer = new ddejonge.bandana.DDAgent2.DDAgent2(args);

        myPlayer.run();
    }

    public DDAgent2(String[] args) {
        super(args);

        dBraneTactics = this.getTacticalModule();
    }

    @Override
    public void start() {
        this.getLogger().logln("game is starting!", printToConsole);
    }

    @Override
    public void negotiate(long negotiationDeadline) {
    }

    @Override
    public void receivedOrder(Order arg0) {
    }
}
