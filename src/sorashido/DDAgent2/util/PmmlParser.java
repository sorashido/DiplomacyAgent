//package sorashido.DDAgent2.util;
//
//import org.dmg.pmml.PMML;
//import org.jpmml.evaluator.ModelEvaluator;
//import org.jpmml.evaluator.ModelEvaluatorFactory;
//import org.jpmml.model.PMMLUtil;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//
//
//public class PmmlParser {
//    ModelEvaluator m_evaluator;
//
//    PmmlParser(String filename) {
//        try {
//            PMML pmml = readPMML(filename);
//            m_evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Double evaluate(){
////        Map map;
////        m_evaluator.evaluate();
//        return 0.0;
//    }
//
//    private PMML readPMML(String filename) throws Exception {
//        try(InputStream is = new FileInputStream(filename)){
//            return PMMLUtil.unmarshal(is);
//        }
//    }
//}