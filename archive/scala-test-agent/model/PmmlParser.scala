package sorashido.DDAgent3.util

import java.io.{File, FileInputStream}
import java.util

import scala.collection.JavaConversions._
import org.dmg.pmml.{FieldName, PMML}
import org.jpmml.evaluator.{Evaluator, ModelEvaluatorFactory}
import org.jpmml.model.{ImportFilter, JAXBUtil}
import org.xml.sax.InputSource

object Predictable {

  import java.io.File
  import java.io.FileInputStream

  val pmml = readPMML("./model/LogisticRegressionDip.pmml")
  val evaluator = prepModelEvaluator(pmml)

  def main(args: Array[String]): Unit ={
    val map = Map("x1"->2.1f)
    val m = mapAsJavaMap(map).asInstanceOf[java.util.Map[FieldName, java.lang.Float]]

    print(getClass.getResource("").getPath)
  }

  def predict(): String = {
    val map = Map("x1"->2.1f, "x2" -> 2.1f)
    val m = mapAsJavaMap(map).asInstanceOf[java.util.Map[FieldName, java.lang.Float]]
    evaluator.evaluate(m).get("y").toString
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