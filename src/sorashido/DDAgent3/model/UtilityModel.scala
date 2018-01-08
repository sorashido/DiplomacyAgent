package sorashido.DDAgent3.model

import java.util.ArrayList

import ddejonge.bandana.dbraneTactics.DBraneTactics
import ddejonge.bandana.negoProtocol.BasicDeal
import es.csic.iiia.fabregues.dip.board.{Game, Power}

class UtilityModel {
  def calcPlanValue(game: Game, me: Power, dBraneTactics:DBraneTactics, commitments: ArrayList[BasicDeal]): Double = {
    val myPlan = dBraneTactics.determineBestPlan(game, me, commitments)
    if (myPlan == null) { //取り決めのために行動できない -> 交渉する必要なし
      return 0.0
    }
    myPlan.getValue
  }
}