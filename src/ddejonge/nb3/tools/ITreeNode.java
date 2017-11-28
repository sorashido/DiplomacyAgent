// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ITreeNode.java

package ddejonge.nb3.tools;

import java.util.List;

public interface ITreeNode
{

    public abstract ITreeNode getParent();

    public abstract List getChildren();
}
