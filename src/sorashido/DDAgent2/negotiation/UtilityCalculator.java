package sorashido.DDAgent2.negotiation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class UtilityCalculator {

    private HashMap<String, String> out1 = new HashMap<>();
    private HashMap<String, String> out2 = new HashMap<>();

    public UtilityCalculator() throws Exception {
        readCsv("location.csv");
    }

    public HashMap<String, Integer> getwinlocation(int year, String season, String country, int state, int mode){
        HashMap<String, Integer> ans = new HashMap<>();

        HashMap<String, Integer> seasonID = new HashMap<String, Integer>() {{ put("SPR", 0); put("FAL", 1);}};
        HashMap<String, Integer> countryID = new HashMap<String, Integer>() {{ put("ENG", 0); put("FRA", 1); put("ITA", 2);
            put("GER", 3);put("AUT", 4); put("TUR", 5); put("RUS", 6);}};

        if(!seasonID.containsKey(season) || !countryID.containsKey(country)){ return ans; }

        if(year!= 1920) year += 1;

        String key = Integer.toString(year) +  season + country.substring(0,1) + Integer.toString(state);
        String[] temp;
        if(mode == 0 || out1.containsKey(key)) temp = out1.get(key).split(":", 0);
        else temp = out2.get(key).split(":", 0);
        for(String t : temp){
            if(t.length() > 3){
                String l = t.replaceAll("[0-9]", "");
                String c = t.replaceAll("[^0-9]", "");
                if(!c.isEmpty())ans.put(l, Integer.valueOf(c));
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
                out1.put(temp[1],temp[2]);
                out2.put(temp[1],temp[3]);
            }
        } catch (IOException ignored) {
        }
    }
}