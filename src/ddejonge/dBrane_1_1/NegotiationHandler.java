// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NegotiationHandler.java

package ddejonge.dBrane_1_1;

import ddejonge.nb3.algorithm.Nb3Proposal;
import es.csic.iiia.fabregues.dip.board.Power;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package ddejonge.dBrane_1_1:
//            DBraneNegotiator

public class NegotiationHandler
{

    NegotiationHandler(DBraneNegotiator negotiator, Power me, int negoPort, boolean negotiates)
    {
        dBranes = new ArrayList();
    }

    void waitTillReady()
    {
    }

    void proposeDeal(Nb3Proposal nb3proposal)
        throws IOException
    {
    }

    void acceptDeal(Nb3Proposal nb3proposal)
        throws IOException
    {
    }

    public static String actions2String(List actions)
    {
        return actions.toString();
    }

    ArrayList dBranes;
}
