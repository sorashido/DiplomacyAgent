package sorashido.DDAgent2;

import ddejonge.bandana.anac.ANACNegotiator;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.negoProtocol.DiplomacyNegoClient;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.orders.Order;
import sorashido.DDAgent2.util.Constants;

import java.util.Random;

public class DDAgent2 extends ANACNegotiator {

    final boolean printToConsole = false;
    final Constants constants = new Constants();

    DBraneTactics dBraneTactics = new DBraneTactics();
    private Random random = new Random();

    public static void main(String[] args) {


        sorashido.DDAgent2.DDAgent2 myPlayer = new sorashido.DDAgent2.DDAgent2(args);

        myPlayer.run();
    }

    private DDAgent2(String[] args) {
        super(args);

        dBraneTactics = this.getTacticalModule();
    }

    @Override
    public void start() {

        this.getLogger().logln("game is starting!", printToConsole);
    }

    @Override
    public void negotiate(long negotiationDeadline) {
        while (System.currentTimeMillis() < negotiationDeadline) {
            //
            while (hasMessage()) {
                manageProposedMessage();
            }
            //
            proposeMessage();
        }
    }

    private void manageProposedMessage() {
        Message receivedMessage = removeMessageFromQueue();

        //accepted
        if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.ACCEPT)) {
            DiplomacyProposal acceptedProposal = (DiplomacyProposal) receivedMessage.getContent();
            this.getLogger().logln("DDAgent2.negotiate() Received acceptance from " + receivedMessage.getSender() + ": " + acceptedProposal, printToConsole);
        }//proposed
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.PROPOSE)) {
            DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();

        }//confirmed
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.CONFIRM)) {
            DiplomacyProposal confirmedProposal = (DiplomacyProposal) receivedMessage.getContent();

        }//rejected
        else if (receivedMessage.getPerformative().equals(DiplomacyNegoClient.REJECT)) {
            DiplomacyProposal rejectedProposal = (DiplomacyProposal) receivedMessage.getContent();

        } else {
            //We have received any other kind of message.
            this.getLogger().logln("Received a message of unhandled type: " + receivedMessage.getPerformative() + ". Message content: " + receivedMessage.getContent().toString(), printToConsole);
        }
    }

    private void proposeMessage() {
        //
        final double eps = 0.1;
        if (random.nextDouble() < eps) {
            //random

        } else {
            //bestを探す
        }

    }

    @Override
    public void receivedOrder(Order arg0) {
        // TODO Auto-generated method stub
        if (game.getYear() == 1920 && game.getPhase().equals("WIN")) {
        }
    }

}
