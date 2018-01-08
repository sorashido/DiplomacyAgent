package sorashido.DDAgent3

import ddejonge.bandana.anac.ANACNegotiator
import ddejonge.bandana.dbraneTactics.DBraneTactics
import ddejonge.bandana.dbraneTactics.Plan
import ddejonge.bandana.negoProtocol._
import ddejonge.bandana.tools.Utilities
import ddejonge.negoServer.Message
import es.csic.iiia.fabregues.dip.orders._
import es.csic.iiia.fabregues.dip.board.{Power, Province, Region}
import java.util.Random
import java.util.ArrayList

import scala.collection.JavaConversions._
import sorashido.DDAgent3.util.{Constants, Predictable}
import sorashido.DDAgent3.negotiation.ProposeDeal
import sorashido.DDAgent3.model.UtilityModel

object DDAgent3 {
  def main(args: Array[String]): Unit = {
    val myPlayer = new DDAgent3(args)
    myPlayer.run()
  }
}

class DDAgent3 private(val args: Array[String]) extends ANACNegotiator(args) {
//  var dBraneTactics = this.getTacticalModule
  final private[DDAgent3] val printToConsole = true
//  final private[DDAgent3] val constants = new Constants
  final private[DDAgent3] val constants = Constants
  private[DDAgent3] var dBraneTactics = new DBraneTactics
  private val random = new Random
  val proposedeal = new ProposeDeal()
  val utilitymodel = new UtilityModel()

  override def start(): Unit = {
    this.getLogger.logln("game is starting!", printToConsole)
  }

  override def negotiate(negotiationDeadline: Long): Unit = {
    while (System.currentTimeMillis < negotiationDeadline) {
      while (hasMessage) manageProposedMessage()
//
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
      val deal = receivedProposal.getProposedDeal.asInstanceOf[BasicDeal]

      var outDated = false
      for (dmz <- deal.getDemilitarizedZones) {
        if (isHistory(dmz.getPhase, dmz.getYear)) {
          outDated = true
        }
      }
      for (orderCommitment <- deal.getOrderCommitments) {
        if (isHistory(orderCommitment.getPhase, orderCommitment.getYear)) {
          outDated = true
        }
      }

      var consistencyReport = ""
      if (!outDated) {
        val commitments = new ArrayList[BasicDeal]
        commitments.addAll(this.getConfirmedDeals)
        commitments.add(deal)
        consistencyReport = Utilities.testConsistency(game, commitments)

        if (consistencyReport == "") {
          val opponent = game.getPower(receivedMessage.getSender())
          if (utilitymodel.calcPlanValue(game, me, dBraneTactics, commitments) > 0.5) {
            this.acceptProposal(receivedProposal.getId)
            this.getLogger.logln("DDAgent3.negotiate()  Accepting: " + receivedProposal, printToConsole)
          }
        }
      }
    }
    else if (receivedMessage.getPerformative == DiplomacyNegoClient.CONFIRM) {
      val confirmedProposal = receivedMessage.getContent.asInstanceOf[DiplomacyProposal]
      this.getLogger.logln("DDAgent3.negotiate()  Confirm: " + confirmedProposal, printToConsole)
    }
    else if (receivedMessage.getPerformative == DiplomacyNegoClient.REJECT) {
      val rejectedProposal = receivedMessage.getContent.asInstanceOf[DiplomacyProposal]
      this.getLogger.logln("DDAgent3.negotiate()  Reject: " + rejectedProposal, printToConsole)
    }
    else {
      this.getLogger.logln("Received a message of unhandled type: " + receivedMessage.getPerformative + ". Message content: " + receivedMessage.getContent.toString, printToConsole)
    }
  }

  private def proposeMessage(): Unit = {
    val newDealToPropose = proposedeal.searchForNewDealToPropose(game, me, dBraneTactics, this.getConfirmedDeals, this.getNegotiatingPowers)
  }

//  def calcPlanValue(commitments: ArrayList[BasicDeal]): Double = {
//    val myPlan = this.dBraneTactics.determineBestPlan(this.game, me, commitments)
//    if (myPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
//      return 0.0
//    }
//    myPlan.getValue
//  }
}
