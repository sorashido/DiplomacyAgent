// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Utils.java

package ddejonge.tools;

import java.io.PrintStream;
import java.util.List;
import java.util.Random;

public class Utils
{

    public Utils()
    {
    }

    public static void main(String args[])
    {
        double d = 13.8937464847484D;
        System.out.println((new StringBuilder("original: ")).append(d).toString());
        System.out.println((new StringBuilder("rounded: ")).append(round(d, 1)).toString());
    }

    public static void sleepTight(long millis)
    {
        long endTime = System.currentTimeMillis() + millis;
        try
        {
            Thread.sleep(millis);
        }
        catch(InterruptedException e)
        {
            long timeLeft = endTime - System.currentTimeMillis();
            if(timeLeft > 0L)
                sleepTight(timeLeft);
        }
    }

    public static void sleepLight(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch(InterruptedException interruptedexception) { }
    }

    public static String toBinaryString(long x)
    {
        String s = "";
        boolean trailing = true;
        for(int i = 63; i >= 0; i--)
        {
            long n = 1 << i;
            if((n & x) == n)
            {
                s = (new StringBuilder(String.valueOf(s))).append("1").toString();
                trailing = false;
            } else
            if(!trailing)
                s = (new StringBuilder(String.valueOf(s))).append("0").toString();
        }

        return s;
    }

    public static double round(double number, int numDigits)
    {
        if(numDigits < 0)
            throw new IllegalArgumentException("Number of digits bust be greater than or equal to 0.");
        int powerOfTen = 1;
        for(int i = 0; i < numDigits; i++)
            powerOfTen *= 10;

        double returnVal = number * (double)powerOfTen;
        returnVal = Math.round(returnVal);
        returnVal /= powerOfTen;
        return returnVal;
    }

    public static Object pickRandomObjectFromList(List list, Random random)
    {
        int i = random.nextInt(list.size());
        return list.get(i);
    }
}
