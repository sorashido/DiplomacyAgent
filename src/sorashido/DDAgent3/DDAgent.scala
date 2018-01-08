package sorashido.DDAgent3

import ddejonge.bandana.anac.ANACNegotiator
import ddejonge.bandana.dbraneTactics.DBraneTactics
import ddejonge.bandana.negoProtocol._
import ddejonge.bandana.tools.Utilities
import es.csic.iiia.fabregues.dip.orders.{HLDOrder, MTOOrder, Order}
import es.csic.iiia.fabregues.dip.board.{Power, Province, Region}
import java.util.Random
import java.util.ArrayList

import scala.collection.JavaConversions._
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
          if (calcPlanValue(commitments, opponent) > 0.5) {
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
    val newDealToPropose = searchForNewDealToPropose
    if (!newDealToPropose.getDemilitarizedZones.isEmpty || !newDealToPropose.getOrderCommitments.isEmpty) {
      this.getLogger.logln("DDAgent3.negotiate() Proposing: " + newDealToPropose, printToConsole)
      this.proposeDeal(newDealToPropose)
    }
  }

  def searchForNewDealToPropose: BasicDeal = {
    val commitments = new ArrayList[BasicDeal]
    var bestValue = 1.0
    commitments.addAll(this.getConfirmedDeals)
    var bestDeal = generateRandomDeal
    for(i <- 0 to 9) {
      val randomDeal = generateRandomDeal
      if (randomDeal != null) {
        commitments.add(randomDeal)
        val value = calcPlanValue(commitments)
        if (value > bestValue) {
          bestDeal = randomDeal
          bestValue = value
        }
        commitments.remove(commitments.size - 1)
      }
    }
    bestDeal
  }

  def generateRandomDeal: BasicDeal = { //Get the names of all the powers that are connected to the negotiation server and which have not been eliminated.
    val aliveNegotiatingPowers = this.getNegotiatingPowers
    val numAliveNegoPowers = aliveNegotiatingPowers.size
    if (numAliveNegoPowers < 2) return null
    val demilitarizedZones = new ArrayList[DMZ](3)

    for(i <- 0 to 2) {
      val powers = new ArrayList[Power](2)
      powers.add(me)
      var randomPower = me
      while ( {
        randomPower.equals(me)
      }) {
        val numNegoPowers = aliveNegotiatingPowers.size
        randomPower = aliveNegotiatingPowers.get(random.nextInt(numNegoPowers))
      }
      powers.add(randomPower)

      val provinces = new ArrayList[Province]
      for(j <- 0 to 2) {
        val numProvinces = this.game.getProvinces.size
        val randomProvince = this.game.getProvinces.get(random.nextInt(numProvinces))
        provinces.add(randomProvince)
      }
      demilitarizedZones.add(new DMZ(game.getYear, game.getPhase, powers, provinces))
    }

    val randomOrderCommitments = new ArrayList[OrderCommitment]
    val units = new ArrayList[Region]
    for (power <- aliveNegotiatingPowers) {
      units.addAll(power.getControlledRegions)
    }

    for(i <- 0 to 2) {
      if (units.size != 0) {
        val randomUnit = units.remove(random.nextInt(units.size))
        val power = game.getController(randomUnit)
        val potentialDestinations = new ArrayList[Region]
        val adjacentRegions = randomUnit.getAdjacentRegions
        adjacentRegions.add(randomUnit)
        for (adjacentRegion <- adjacentRegions) {
          val adjacentProvince = adjacentRegion.getProvince
          //Check that the adjacent Region is not demilitarized for the power controlling the unit.
          var isDemilitarized = false
          for (dmz <- demilitarizedZones) {
            if (dmz.getPowers.contains(power) && dmz.getProvinces.contains(adjacentProvince)) {
              isDemilitarized = true
            }
          }
          if (!isDemilitarized) potentialDestinations.add(adjacentRegion)
        }
        val numPotentialDestinations = potentialDestinations.size
        if (numPotentialDestinations > 0) {
          val randomDestination = potentialDestinations.get(random.nextInt(numPotentialDestinations))
          if (randomDestination.equals(randomUnit)) {
            val randomOrder = new HLDOrder(power, randomUnit)
            randomOrderCommitments.add(new OrderCommitment(game.getYear, game.getPhase, randomOrder))
          }
          else {
            val randomOrder = new MTOOrder(power, randomUnit, randomDestination)
            randomOrderCommitments.add(new OrderCommitment(game.getYear, game.getPhase, randomOrder))
          }
        }
      }
    }
    val deal = new BasicDeal(randomOrderCommitments, demilitarizedZones)
    deal
  }

  private def calcPlanValue(commitments: ArrayList[BasicDeal], opponent: Power): Double = {
    val myPlan = this.dBraneTactics.determineBestPlan(game, me, commitments)
    val opPlan = this.dBraneTactics.determineBestPlan(game, opponent, commitments)
    if (myPlan == null || opPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
      return 0.0
    }
    myPlan.getValue
  }

  private def calcPlanValue(commitments: ArrayList[BasicDeal]): Double = {
    val myPlan = this.dBraneTactics.determineBestPlan(game, me, commitments)
    if (myPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
      return 0.0
    }
    myPlan.getValue
  }
}
