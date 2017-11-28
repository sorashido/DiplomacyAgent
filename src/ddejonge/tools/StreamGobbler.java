// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StreamGobbler.java

package ddejonge.tools;

import java.io.*;

class StreamGobbler extends Thread
{

    StreamGobbler(InputStream is, String type, boolean print)
    {
        inputStream = is;
        this.type = type;
        outputStream = null;
        this.print = print;
    }

    StreamGobbler(InputStream is, String type, OutputStream redirect)
    {
        inputStream = is;
        this.type = type;
        outputStream = redirect;
        print = false;
    }

    public void run()
    {
        try
        {
            PrintWriter pw = null;
            if(outputStream != null)
                pw = new PrintWriter(outputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            for(String line = null; (line = br.readLine()) != null;)
            {
                if(pw != null)
                    pw.println((new StringBuilder("StreamGobbler ")).append(type).append(": ").append(line).toString());
                if(print)
                    System.out.println((new StringBuilder("StreamGobbler ")).append(type).append(": ").append(line).toString());
            }

            if(pw != null)
            {
                pw.flush();
                pw.close();
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    InputStream inputStream;
    String type;
    OutputStream outputStream;
    boolean print;
}
