// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3Message.java

package ddejonge.nb3.domain;

import ddejonge.nb3.algorithm.Nb3AgentSet;
import java.util.ArrayList;

// Referenced classes of package ddejonge.nb3.domain:
//            Nb3InstanceInfo

public abstract class Nb3Message
{

    public Nb3Message()
    {
    }

    public Nb3InstanceInfo.MsgType getPerformative()
    {
        return type;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String cont)
    {
        content = cont;
    }

    public int getSender()
    {
        return sender;
    }

    public String getConversationId()
    {
        return conversationId;
    }

    protected void addReceiver(int ag)
    {
        receivers.add(ag);
    }

    protected void removeReceiver(int ag)
    {
        receivers.remove(ag);
    }

    public abstract ArrayList getActions();

    protected Nb3AgentSet receivers;
    protected Nb3InstanceInfo.MsgType type;
    protected String content;
    protected int sender;
    protected String conversationId;
}
