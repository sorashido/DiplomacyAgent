package sorashido.DDAgent3

import ddejonge.bandana.anac.ANACNegotiator
import ddejonge.bandana.dbraneTactics.DBraneTactics
import ddejonge.bandana.negoProtocol.DiplomacyNegoClient
import ddejonge.bandana.negoProtocol.DiplomacyProposal
import ddejonge.negoServer.Message
import es.csic.iiia.fabregues.dip.orders.Order
import java.util.Random

import sorashido.DDAgent3.util.{Constants, Predictable}


object DDAgent3 {
  def main(args: Array[String]): Unit = {
    val myPlayer = new DDAgent3(args)
    myPlayer.run()
  }
}

class DDAgent3 private(val args: Array[String]) extends ANACNegotiator(args) {
  dBraneTactics = this.getTacticalModule
  final private[DDAgent3] val printToConsole = false
//  final private[DDAgent3] val constants = new Constants
  final private[DDAgent3] val constants = Constants
  private[DDAgent3] var dBraneTactics = new DBraneTactics
  private val random = new Random

  override def start(): Unit = {
    this.getLogger.logln("game is starting!", printToConsole)
  }

  override def negotiate(negotiationDeadline: Long): Unit = {
    while (System.currentTimeMillis < negotiationDeadline) {
      while (hasMessage) manageProposedMessage()

      proposeMessage()

      val predict = Predictable
    }
  }

  override def receivedOrder(arg0: Order): Unit = {
    if (game.getYear == 1920 && game.getPhase == "WIN") {
    }
  }

  // メッセージ
  private def manageProposedMessage(): Unit = {
    val receivedMessage = removeMessageFromQueue
    if (receivedMessage.getPerformative == DiplomacyNegoClient.ACCEPT) {
      val acceptedProposal = receivedMessage.getContent.asInstanceOf[DiplomacyProposal]
      this.getLogger.logln("DDAgent3.negotiate() Received acceptance from " + receivedMessage.getSender + ": " + acceptedProposal, printToConsole)
    }
    else if (receivedMessage.getPerformative == DiplomacyNegoClient.PROPOSE) {
      val receivedProposal = receivedMessage.getContent.asInstanceOf[DiplomacyProposal]
    }
    else if (receivedMessage.getPerformative == DiplomacyNegoClient.CONFIRM) {
      val confirmedProposal = receivedMessage.getContent.asInstanceOf[DiplomacyProposal]
    }
    else if (receivedMessage.getPerformative == DiplomacyNegoClient.REJECT) {
      val rejectedProposal = receivedMessage.getContent.asInstanceOf[DiplomacyProposal]
    }
    else this.getLogger.logln("Received a message of unhandled type: " + receivedMessage.getPerformative + ". Message content: " + receivedMessage.getContent.toString, printToConsole)
  }

  // 提案
  private def proposeMessage(): Unit = {

  }
}
