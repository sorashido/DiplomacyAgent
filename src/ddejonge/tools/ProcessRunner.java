// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProcessRunner.java

package ddejonge.tools;


// Referenced classes of package ddejonge.tools:
//            StreamGobbler

public class ProcessRunner
{

    public ProcessRunner()
    {
    }

    public static Process exec(String cmdArray[], String name)
    {
        Process process = null;
        try
        {
            process = Runtime.getRuntime().exec(cmdArray);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), (new StringBuilder(String.valueOf(name))).append(" ERROR").toString(), true);
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), (new StringBuilder(String.valueOf(name))).append(" OUTPUT").toString(), true);
            errorGobbler.start();
            outputGobbler.start();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return process;
    }
}
