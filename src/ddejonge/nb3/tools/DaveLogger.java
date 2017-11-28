// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DaveLogger.java

package ddejonge.nb3.tools;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DaveLogger
{

    public DaveLogger()
    {
        enabled = false;
        capacity = 1000;
        content = new ArrayList(capacity);
    }

    public DaveLogger(int _capacity)
    {
        enabled = false;
        capacity = 1000;
        content = new ArrayList(capacity);
        capacity = _capacity;
    }

    public void enable(String _folderPath, String _fileName, boolean useTimeStamp)
    {
        if(useTimeStamp)
            _fileName = (new StringBuilder(String.valueOf(getDateString()))).append(" ").append(_fileName).toString();
        enable(_folderPath, _fileName);
    }

    public void enable(String folderPath, String fileName)
    {
        if(!enabled)
        {
            content = new ArrayList(capacity);
            content.add("");
        }
        enabled = true;
        this.folderPath = folderPath;
        this.fileName = fileName;
    }

    public static String getDateString()
    {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
        return sdf.format(now.getTime());
    }

    public void logln()
    {
        logln("");
    }

    public void logln(String s)
    {
        logln(s, false);
    }

    public void logln(String s, boolean print)
    {
        if(!enabled)
            return;
        if(print)
            System.out.println(s);
        content.add(s);
    }

    public void log(String s)
    {
        log(s, false);
    }

    public void log(String s, boolean print)
    {
        if(!enabled)
            return;
        if(print)
            System.out.print(s);
        String s2 = (String)content.get(content.size() - 1);
        s2 = (new StringBuilder(String.valueOf(s2))).append(s).toString();
        content.remove(content.size() - 1);
        content.add(s2);
    }

    public void log(int n)
    {
        log(Integer.toString(n));
    }

    public void log(int n, boolean print)
    {
        log(Integer.toString(n), print);
    }

    public void println(String s)
    {
        if(!enabled)
        {
            return;
        } else
        {
            System.out.println(s);
            return;
        }
    }

    public void println(int n)
    {
        if(!enabled)
        {
            return;
        } else
        {
            System.out.println(Integer.toString(n));
            return;
        }
    }

    public void clearErrorInfo()
    {
        errorInfo = new ArrayList();
    }

    public void addErrorInfo(String s)
    {
        errorInfo.add(s);
    }

    public void displayErrorInfo()
    {
        System.out.println();
        System.out.println("*Error Info: *");
        String s;
        for(Iterator iterator = errorInfo.iterator(); iterator.hasNext(); System.out.println(s))
            s = (String)iterator.next();

        System.out.println();
    }

    public void writeToFile()
    {
        File file;
        PrintWriter out;
        if(!enabled)
            return;
        File folder = new File(folderPath);
        if(!folder.exists())
            folder.mkdirs();
        file = new File(folder, fileName);
        if(!file.exists())
            try
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                System.out.println((new StringBuilder("DaveLogger.writeToFile() folderPath: ")).append(folderPath).toString());
                System.out.println((new StringBuilder("DaveLogger.writeToFile() fileName: ")).append(fileName).toString());
                e.printStackTrace();
                return;
            }
        out = null;
        try
        {
            out = new PrintWriter(new FileWriter(file, true));
            String s;
            for(Iterator iterator = content.iterator(); iterator.hasNext(); out.println(s))
                s = (String)iterator.next();

            content.clear();
            break MISSING_BLOCK_LABEL_224;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(out != null)
        {
            out.flush();
            out.close();
        }
        break MISSING_BLOCK_LABEL_236;
        Exception exception;
        exception;
        if(out != null)
        {
            out.flush();
            out.close();
        }
        throw exception;
        if(out != null)
        {
            out.flush();
            out.close();
        }
    }

    String folderPath;
    String fileName;
    String filePath;
    public boolean enabled;
    private ArrayList errorInfo;
    int capacity;
    protected ArrayList content;
}
