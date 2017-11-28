// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrintProvinceList.java

package ddejonge.dipgameExtensions;

import es.csic.iiia.fabregues.dip.board.Province;
import gameBuilder.DiplomacyGameBuilder;
import java.io.PrintStream;

// Referenced classes of package ddejonge.dipgameExtensions:
//            DiplomacyGame

public class PrintProvinceList
{

    public PrintProvinceList()
    {
    }

    public static void main(String args[])
    {
        DiplomacyGame game = DiplomacyGameBuilder.createDefaultGame();
        for(int i = 0; i < 75; i++)
        {
            String s = (new StringBuilder()).append(i).append(".").append(DiplomacyGame.getProvince(i).getName()).toString();
            if(DiplomacyGame.getProvince(i).isSC())
                s = (new StringBuilder(String.valueOf(s))).append("*").toString();
            System.out.println(s);
        }

    }
}
