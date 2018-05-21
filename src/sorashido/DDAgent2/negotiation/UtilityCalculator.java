package sorashido.DDAgent2.negotiation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class UtilityCalculator {

    private HashMap<String, String> input = new HashMap<>();
    private HashMap<String, String> out1 = new HashMap<>();
    private HashMap<String, String> out2 = new HashMap<>();

    public UtilityCalculator() throws Exception {
        // read csv
        readCsv("location.csv");
    }

    public String getInput(String key){return input.get(key);}

    public HashMap<String, Integer> getlocation(String key){
        HashMap<String, Integer> ans = new HashMap<>();

        String[] temp = out1.get(key).split(":", 0);
        for(String t : temp){
            String[] s = t.split("[0-9]", 2);
            ans.put(s[0], Integer.valueOf(s[1]));
        }
        return ans;
    }

    public HashMap<String, Integer> getwinlocation(String key){
        HashMap<String, Integer> ans = new HashMap<>();

        String[] temp = out2.get(key).split(":", 0);
        for(String t : temp){
            String[] s = t.split("[0-9]", 2);
            ans.put(s[0], Integer.valueOf(s[1]));
        }
        return ans;
    }

    // read utility
    private void readCsv(String name) {
        try {
            InputStream is = getClass().getResourceAsStream(name);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(",",0);
                input.put(temp[0],temp[1]);
                out1.put(temp[0],temp[2]);
                out2.put(temp[0],temp[3]);
//                System.out.print(temp[0]+","+temp[1]+","+temp[2]+","+temp[3]);
//                System.out.println("");
            }
        } catch (IOException ignored) {
        }
    }
}