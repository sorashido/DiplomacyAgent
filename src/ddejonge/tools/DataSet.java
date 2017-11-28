// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DataSet.java

package ddejonge.tools;

import java.util.ArrayList;
import java.util.Iterator;

public class DataSet
{

    public DataSet()
    {
        values = new ArrayList();
        needsCalculation = true;
    }

    public void addValue(double val)
    {
        values.add(Double.valueOf(val));
        needsCalculation = true;
    }

    public void addValue(double val, int frequency)
    {
        if(frequency <= 0)
            throw new IllegalArgumentException("DataSet.addValue() Error! frequency must be a number larger than 0.");
        for(int i = 0; i < frequency; i++)
            values.add(Double.valueOf(val));

        needsCalculation = true;
    }

    private void calculate()
    {
        if(!needsCalculation)
            return;
        sum = 0.0D;
        numValues = values.size();
        for(Iterator iterator = values.iterator(); iterator.hasNext();)
        {
            Double d = (Double)iterator.next();
            sum += d.doubleValue();
        }

        mean = sum / (double)numValues;
        double sumDiffSquared = 0.0D;
        for(Iterator iterator1 = values.iterator(); iterator1.hasNext();)
        {
            Double d = (Double)iterator1.next();
            double diffSquared = (d.doubleValue() - mean) * (d.doubleValue() - mean);
            sumDiffSquared += diffSquared;
        }

        stdDiv = Math.sqrt(sumDiffSquared / (double)(numValues - 1));
        stdError = stdDiv / Math.sqrt(numValues);
        needsCalculation = false;
    }

    public double getMean()
    {
        calculate();
        return mean;
    }

    public double getStdDiv()
    {
        calculate();
        return stdDiv;
    }

    public double getStdError()
    {
        calculate();
        return stdError;
    }

    ArrayList values;
    double sum;
    int numValues;
    double mean;
    double stdDiv;
    double stdError;
    boolean needsCalculation;
}
