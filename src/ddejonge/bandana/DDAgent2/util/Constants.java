package ddejonge.bandana.DDAgent2.util;

import java.util.HashMap;

public class Constants {
    public HashMap<String, Integer> regionNumber =
            new HashMap<String, Integer>(){
            {
                //補給地
                put("LVP",0);put("LON",1);put("EDI",2);put("BER",3);put("MUN",4);
                put("KIE",5);put("MOS",6);put("WAR",7);put("STP",8);put("SEV",9);
                put("CON",10);put("SMY",11);put("ANK",12);put("VIE",13);put("BUD",14);
                put("TRI",15);put("ROM",16);put("VEN",17);put("NAP",18);put("PAR",19);
                put("MAR",20);put("BRE",21);
                //
                put("POR",22);put("SPA",23);put("SWE",24);put("BEL",25);put("HOL",26);
                put("SER",27);put("BUL",28);put("GRE",29);put("RUM",30);put("DEN",31);
                put("NWY",32);put("TUN",33);

                //その他
//                put("ALB",0);put("APU",2);put("ARM",3);put("BOH",4);put("BUR",6);
//                put("CLY",7);put("FIN",11);put("GAL",12);put("GAS",13);put("LIV",16);
//                put("NAF",17);put("PIC",19);put("PIA",20);put("PRU",22);
//                put("RUH",23);put("SIL",27);put("SYR",31);put("TUS",33);put("TYR",34);
//                put("UKR",30);put("WAL",31);put("YOR",32);
                //海域
//                put("ADS",33);put("AES",34);put("BAR",33);put("BAL",34);put("BLA",33);
//                put("EMS",34);put("ENC",33);put("GOB",34);put("GOL",33);put("HEL",34);
//                put("IOS",33);put("IRS",34);put("MAO",33);put("NAO",34);put("NTH",33);
//                put("NWG",34);put("SKA",33);put("TYS",34);put("WMS",33);
            }
    };

}
