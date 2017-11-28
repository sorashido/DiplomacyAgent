// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nb3InstanceInfo.java

package ddejonge.nb3.domain;

import java.util.HashMap;

public class Nb3InstanceInfo
{
    public static final class MsgType extends Enum
    {

        public static MsgType[] values()
        {
            MsgType amsgtype[];
            int i;
            MsgType amsgtype1[];
            System.arraycopy(amsgtype = ENUM$VALUES, 0, amsgtype1 = new MsgType[i = amsgtype.length], 0, i);
            return amsgtype1;
        }

        public static MsgType valueOf(String s)
        {
            return (MsgType)Enum.valueOf(ddejonge/nb3/domain/Nb3InstanceInfo$MsgType, s);
        }

        public static final MsgType PROPOSE;
        public static final MsgType ACCEPT;
        public static final MsgType REFUSE;
        public static final MsgType CONFIRM;
        public static final MsgType REJECT;
        private static final MsgType ENUM$VALUES[];

        static 
        {
            PROPOSE = new MsgType("PROPOSE", 0);
            ACCEPT = new MsgType("ACCEPT", 1);
            REFUSE = new MsgType("REFUSE", 2);
            CONFIRM = new MsgType("CONFIRM", 3);
            REJECT = new MsgType("REJECT", 4);
            ENUM$VALUES = (new MsgType[] {
                PROPOSE, ACCEPT, REFUSE, CONFIRM, REJECT
            });
        }

        private MsgType(String s, int i)
        {
            super(s, i);
        }
    }


    public Nb3InstanceInfo()
    {
    }

    public static int getCurrentTime()
    {
        return (int)(System.currentTimeMillis() - startTime);
    }

    public static int name2int(String name)
    {
        return ((Integer)_name2int.get(name)).intValue();
    }

    public static String agentNames[];
    protected static HashMap _name2int;
    public static int numAgents;
    public static long startTime;
    public static long endTime;
}
