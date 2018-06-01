package sorashido.DDAgent2.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

// 自身のutil, 他の国のutil, 敵対関数を作る
public class DipModel {
    private HashMap<String, Double> correlation = new HashMap<>();;

    private HashMap<String, Double> opthreshold = new HashMap<>();;

    public DipModel() throws Exception {
        readCsv("cor.csv");

        opthreshold.put("ENG", 0.0);
        opthreshold.put("FRA", 0.0);
        opthreshold.put("ITA", 0.0);
        opthreshold.put("RUS", 0.0);
        opthreshold.put("TUR", 0.0);
        opthreshold.put("GER", 0.0);
        opthreshold.put("AUS", 0.0);
    }

    public Double getThreshold(String power){
        return opthreshold.get(power);
    }

    public void updateThreshold(int year, String power, int supplynum){
        Double value = (double)supplynum/18 - 0.12;
        opthreshold.put(power, value);
    }

//    public Double getCorrelation(String name){
//        return correlation.get(name) - opcorrelation.get(name);
//    }

    // nameは自国名+相手国名
//    public void updateCorrelation(String name, Double value){
//        correlation.put(name, value);
//    }

    // nameは自国名+相手国名
//    public Double updateOpCorrelation(String name, Double value){
//        return opcorrelation.put(name, value);
//    }

    // read correlation
    private void readCsv(String name) {
        try {
            InputStream is = getClass().getResourceAsStream(name);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String line;
            String[] country = br.readLine().split(",", 0);
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(",",0);
                correlation.put(temp[0]+country[1], Double.valueOf(temp[1]));
                correlation.put(temp[0]+country[2], Double.valueOf(temp[2]));
                correlation.put(temp[0]+country[3], Double.valueOf(temp[3]));
                correlation.put(temp[0]+country[4], Double.valueOf(temp[4]));
                correlation.put(temp[0]+country[5], Double.valueOf(temp[5]));
                correlation.put(temp[0]+country[6], Double.valueOf(temp[6]));
                correlation.put(temp[0]+country[7], Double.valueOf(temp[7]));
            }
        } catch (IOException ignored) {
        }
    }
}
