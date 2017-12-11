package sorashido.DDAgent3.util

import java.io.{File, FileInputStream}
import scala.collection.JavaConversions._
import org.dmg.pmml.{FieldName, PMML}
import org.jpmml.evaluator.{Evaluator, ModelEvaluatorFactory}
import org.jpmml.model.{ImportFilter, JAXBUtil}
import org.xml.sax.InputSource

object Predictable {
  def main(args: Array[String]): Unit ={
    val pmml = readPMML("./src/sorashido/DDAgent3/util/LogisticRegressionDip.pmml")
    val evaluator = prepModelEvaluator(pmml)
//    val map = Map("x1"->2.1f)
//    val m = mapAsJavaMap(map).asInstanceOf[java.util.Map[FieldName, java.lang.Float]]
//    evaluator.evaluate(m)
  }

  def predict(): Double = {

  }

  def readPMML(filename: String): PMML = {
    val is = new FileInputStream(new File(filename))
    try {
      val source = ImportFilter.apply(new InputSource(is))
      return JAXBUtil.unmarshalPMML(source)
    } finally {
      is.close()
    }
  }

  def prepModelEvaluator(pmml: PMML): Evaluator = {
    return ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml)
  }

}