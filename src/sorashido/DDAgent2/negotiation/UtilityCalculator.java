package sorashido.DDAgent2.negotiation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class UtilityCalculator {

//    private HashMap<String, String> input = new HashMap<>();
    private HashMap<String, String> out1 = new HashMap<>();
    private HashMap<String, String> out2 = new HashMap<>();

    public UtilityCalculator() throws Exception {
        readCsv("location.csv");
    }

//    public void updateUtility(String name) {
//        readCsv(name);
//    }

//    public String getInput(String key){return input.get(key);}

    public HashMap<String, Integer> getwinlocation(int year, String season, String country, int state, int mode){
        HashMap<String, Integer> ans = new HashMap<>();

        HashMap<String, Integer> seasonID = new HashMap<String, Integer>() {{ put("SPR", 0); put("FAL", 1);}};
        HashMap<String, Integer> countryID = new HashMap<String, Integer>() {{ put("ENG", 0); put("FRA", 1); put("ITA", 2);
            put("GER", 3);put("AUT", 4); put("TUR", 5); put("RUS", 6);}};

        if(!seasonID.containsKey(season) || !countryID.containsKey(country)){ return ans; }

        int season_num = seasonID.get(season);
        int country_num = countryID.get(country);
        if(year!= 1920) year += 1;

//        int key_num = 14*(year - 1901) + (7*season_num) + country_num;
        String key = Integer.toString(year) +  season + country.substring(0,1) + Integer.toString(state);

        String[] temp;
        if(mode == 0) temp = out1.get(key).split(":", 0);
        else temp = out2.get(key).split(":", 0);

        for(String t : temp){
            if(t.length() > 3){
                String l = t.replaceAll("[0-9]", "");
                String c = t.replaceAll("[^0-9]", "");
                ans.put(l, Integer.valueOf(c));
            }
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
//                input.put(temp[0],temp[1]);
                out1.put(temp[1],temp[2]);
                out2.put(temp[1],temp[3]);
            }
        } catch (IOException ignored) {
        }
    }
}