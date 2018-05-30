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
        readCsv("location.csv");
    }

    public String getInput(String key){return input.get(key);}

    public HashMap<String, Integer> getlocation(int year, String season, String country){
        HashMap<String, Integer> ans = new HashMap<>();
        String key = getkey(year, season, country);
        String[] temp = out1.get(key).split(":", 0);
        for(String t : temp){
            String l = t.replaceAll("[0-9]", "");
            String c = t.replaceAll("[^0-9]", "");
            ans.put(l, Integer.valueOf(c));
        }
        return ans;
    }

    public HashMap<String, Integer> getwinlocation(int year, String season, String country){
        HashMap<String, Integer> ans = new HashMap<>();
        String key = getkey(year, season, country);

        String[] temp = out2.get(key).split(":", 0);
        for(String t : temp){
            String l = t.replaceAll("[0-9]", "");
            String c = t.replaceAll("[^0-9]", "");
            ans.put(l, Integer.valueOf(c));
        }
        return ans;
    }

    private String getkey(int year, String season, String country){
        HashMap<String, Integer> seasonID = new HashMap<String, Integer>() {{ put("SPR", 0); put("FAL", 1);}};
        HashMap<String, Integer> countryID = new HashMap<String, Integer>() {{ put("ENG", 0); put("FRA", 1); put("ITA", 2);
                        put("GER", 3);put("AUT", 4); put("TUR", 5); put("RUS", 6);}};

        int season_num = seasonID.get(season);
        int country_num = countryID.get(country);

        int ans = 14*(year - 1901) + (7*season_num) + country_num;
        return Integer.toString(ans);
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
            }
        } catch (IOException ignored) {
        }
    }
}