// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FullPlanIterator.java

package ddejonge.dBrane_1_1;

import java.io.PrintStream;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1:
//            BattlePlan

public class FullPlanIterator
    implements Iterator
{

    public static void main(String args[])
    {
        List sc2BattlePlans[] = new List[4];
        sc2BattlePlans[0] = new ArrayList();
        sc2BattlePlans[0].add("A0");
        sc2BattlePlans[0].add("A1");
        sc2BattlePlans[0].add("A2");
        sc2BattlePlans[2] = new ArrayList();
        sc2BattlePlans[2].add("C0");
        sc2BattlePlans[2].add("C1");
        sc2BattlePlans[2].add("C2");
        sc2BattlePlans[2].add("C3");
        sc2BattlePlans[3] = new ArrayList();
        sc2BattlePlans[3].add("D0");
        sc2BattlePlans[3].add("D1");
        BattlePlan fp[];
        for(FullPlanIterator fpi = new FullPlanIterator(sc2BattlePlans); fpi.hasNext(); System.out.println(Arrays.toString(fp)))
            fp = fpi.next();

    }

    public FullPlanIterator(List sc2BattlePlans[])
    {
        nextIsCalculated = true;
        numDomains = 0;
        for(int sc = 0; sc < sc2BattlePlans.length; sc++)
            if(sc2BattlePlans[sc] != null && sc2BattlePlans[sc].size() > 1)
                numDomains++;

        if(numDomains == 0)
            return;
        convertedMap = new List[numDomains];
        converter = new int[numDomains];
        int i = 0;
        for(int sc = 0; sc < sc2BattlePlans.length; sc++)
            if(sc2BattlePlans[sc] != null && sc2BattlePlans[sc].size() > 1)
            {
                convertedMap[i] = sc2BattlePlans[sc];
                converter[i] = sc;
                i++;
            }

        domainSizes = new int[numDomains];
        for(i = 0; i < numDomains; i++)
            domainSizes[i] = convertedMap[i].size();

        domainSizesSorted = Arrays.copyOf(domainSizes, numDomains);
        Arrays.sort(domainSizesSorted);
        maxDomainSize = domainSizesSorted[numDomains - 1];
        currentEquivalenceClass = new int[numDomains];
        next = new int[numDomains];
        emptyArray = new int[numDomains];
        toReturn = new BattlePlan[sc2BattlePlans.length];
    }

    public boolean hasNext()
    {
        if(numDomains == 0)
            return false;
        if(!nextIsCalculated)
            calculateNext();
        return next != null;
    }

    public BattlePlan[] next()
    {
        int arr[] = getNextArray();
        if(arr == null)
            return null;
        for(int i = 0; i < arr.length; i++)
        {
            int sc = converter[i];
            toReturn[sc] = (BattlePlan)convertedMap[i].get(arr[i]);
        }

        return toReturn;
    }

    private int[] getNextArray()
    {
        if(!nextIsCalculated)
            calculateNext();
        nextIsCalculated = false;
        return next;
    }

    private void calculateNext()
    {
        nextIsCalculated = true;
        do
        {
            incrementPermutation();
            if(next != null)
                break;
            incrementEquivalenceClass();
            if(currentEquivalenceClass != null)
                continue;
            System.out.println();
            System.out.println(Arrays.toString(domainSizesSorted));
            break;
        } while(true);
    }

    private void incrementPermutation()
    {
        if(next == null)
        {
            next = emptyArray;
            copyInto(currentEquivalenceClass, next);
        } else
        {
            incrementPermutationLegal();
            if(next == null)
                return;
        }
        if(!isLegal(next, domainSizes))
            incrementPermutationIllegal();
    }

    private void incrementEquivalenceClass()
    {
        for(int i = 0; i < numDomains; i++)
            if((i == numDomains - 1 || currentEquivalenceClass[i] < currentEquivalenceClass[i + 1]) && currentEquivalenceClass[i] < domainSizesSorted[i] - 1)
            {
                for(int j = 0; j < i; j++)
                    currentEquivalenceClass[j] = 0;

                currentEquivalenceClass[i]++;
                return;
            }

        currentEquivalenceClass = null;
    }

    private void incrementEquivalenceClass_old()
    {
        int i;
        for(i = 0; i < currentEquivalenceClass.length - 1; i++)
            if(currentEquivalenceClass[i] < currentEquivalenceClass[i + 1] && currentEquivalenceClass[i] < maxDomainSize - 1)
            {
                currentEquivalenceClass[i]++;
                return;
            }

        if(currentEquivalenceClass[i] < maxDomainSize - 1)
        {
            for(int j = 0; j < currentEquivalenceClass.length - 1; j++)
                currentEquivalenceClass[j] = 0;

            currentEquivalenceClass[i]++;
            return;
        } else
        {
            currentEquivalenceClass = null;
            return;
        }
    }

    void incrementPermutationLegal()
    {
        int dip;
        for(dip = next.length - 1; dip >= 0;)
        {
            if(--dip == -1)
            {
                next = null;
                return;
            }
            if(next[dip] < next[dip + 1])
                break;
        }

        int switchIndex = -1;
        int switchValue = 0x7fffffff;
        for(int i = dip + 1; i < next.length; i++)
            if(next[i] > next[dip] && next[i] <= switchValue)
            {
                switchIndex = i;
                switchValue = next[i];
            }

        int temp = next[dip];
        next[dip] = switchValue;
        next[switchIndex] = temp;
        int total = dip + next.length;
        int av = total / 2;
        for(int i = dip + 1; i <= av; i++)
        {
            temp = next[i];
            next[i] = next[total - i];
            next[total - i] = temp;
        }

    }

    void incrementPermutationIllegal()
    {
_L4:
        int x;
        int y;
        int i;
        x = next.length;
        for(int i = 0; i < next.length; i++)
        {
            if(next[i] < domainSizes[i])
                continue;
            x = i;
            break;
        }

        if(x == next.length)
            break; /* Loop/switch isn't completed */
        if(x == 0)
        {
            next = null;
            break; /* Loop/switch isn't completed */
        }
        y = -1;
        for(int i = next.length - 1; i >= 0; i--)
        {
            if(next[i] >= domainSizes[x])
                continue;
            y = i;
            break;
        }

        if(y == -1)
        {
            next = null;
            break; /* Loop/switch isn't completed */
        }
        if(y < x - 1)
        {
            Arrays.sort(next, y, next.length);
            permuteCyclicLeft(next, y, x);
            continue; /* Loop/switch isn't completed */
        }
        if(y == x - 1)
        {
            Arrays.sort(next, y, next.length);
            permuteCyclicLeft(next, y, x);
            continue; /* Loop/switch isn't completed */
        }
        y = x;
        boolean possible = false;
        do
        {
            for(int i = --y + 1; i < next.length; i++)
            {
                if(next[i] <= next[y])
                    continue;
                possible = true;
                break;
            }

        } while(y > 0 && !possible);
        if(!possible)
        {
            next = null;
            break; /* Loop/switch isn't completed */
        }
        int originalVal = next[y];
        Arrays.sort(next, y, next.length);
        int k = -1;
        int smallestReplacementVal = 0x7fffffff;
        for(i = y; i < next.length; i++)
            if(next[i] > originalVal && next[i] < smallestReplacementVal)
            {
                smallestReplacementVal = next[i];
                k = i;
            }

        permuteCyclicRight(next, y, k);
        if(next[x] < domainSizes[x])
            continue; /* Loop/switch isn't completed */
        i = x - 1;
          goto _L1
_L5:
        if(next[i] >= domainSizes[x])
            continue; /* Loop/switch isn't completed */
        permuteCyclicLeft(next, i, x);
        if(true) goto _L3; else goto _L2
_L3:
        break; /* Loop/switch isn't completed */
        i--;
_L1:
        if(i > y) goto _L5; else goto _L4
_L2:
    }

    private static void copyInto(int original[], int copy[])
    {
        for(int i = 0; i < original.length; i++)
            copy[i] = original[i];

    }

    private static void permuteCyclicLeft(int original[], int from, int to)
    {
        int temp = original[from];
        for(int i = from; i < to; i++)
            original[i] = original[i + 1];

        original[to] = temp;
    }

    private static void permuteCyclicRight(int original[], int from, int to)
    {
        int temp = original[to];
        for(int i = to; i > from; i--)
            original[i] = original[i - 1];

        original[from] = temp;
    }

    static boolean isLegal(int arrayToTest[], int domainSizes[])
    {
        for(int i = 0; i < arrayToTest.length; i++)
            if(arrayToTest[i] >= domainSizes[i])
                return false;

        return true;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public volatile Object next()
    {
        return next();
    }

    int domainSizes[];
    int domainSizesSorted[];
    int maxDomainSize;
    int numDomains;
    private int currentEquivalenceClass[];
    private int next[];
    private int emptyArray[];
    boolean nextIsCalculated;
    List convertedMap[];
    int converter[];
    BattlePlan toReturn[];
}
