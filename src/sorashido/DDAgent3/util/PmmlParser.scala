package sorashido.DDAgent3.util

import java.io.{File, FileInputStream}

import org.dmg.pmml.PMML
import org.jpmml.evaluator.{Evaluator, ModelEvaluatorFactory}
import org.jpmml.model.{ImportFilter, JAXBUtil}
import org.xml.sax.InputSource

object Predictable {
  def main(args: Array[String]): Unit ={
    val pmml = readPMML("/Users/tela/dev/src/github.com/sorashido/DiplomacyAgent/src/sorashido/DDAgent3/util/LogisticRegressionDip.pmml")
    val evaluator = prepModelEvaluator(pmml)
    print(evaluator.getSummary)
  }

  def readPMML(filename: String): PMML = {
    val is = new FileInputStream(new File(filename))
    try {
      val source = ImportFilter.apply(new InputSource(is))
      return JAXBUtil.unmarshalPMML(source)
    } finally {
      is.close();
    }
  }

  def prepModelEvaluator(pmml: PMML): Evaluator = {
    return ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml)
  }

}