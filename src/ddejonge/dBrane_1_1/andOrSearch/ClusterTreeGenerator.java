// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClusterTreeGenerator.java

package ddejonge.dBrane_1_1.andOrSearch;

import ddejonge.dBrane.tools.ArrayOfLists;
import ddejonge.dBrane.tools.TreeNode;
import java.util.*;

// Referenced classes of package ddejonge.dBrane_1_1.andOrSearch:
//            Graph, VariableCluster, Variable

public abstract class ClusterTreeGenerator
{

    public ClusterTreeGenerator()
    {
    }

    public static List createClusterTrees(Graph graph, ArrayOfLists domains)
    {
        List bestClusterTree = null;
        int bestClusterTreeSize = 0x7fffffff;
        for(int i = 0; i < 5; i++)
        {
            List clusterTree = createClusterTreesOnce(graph);
            int size = calculateSize(clusterTree, domains);
            if(size < bestClusterTreeSize)
            {
                bestClusterTree = clusterTree;
                bestClusterTreeSize = size;
            }
        }

        return bestClusterTree;
    }

    private static List createClusterTreesOnce(Graph graph)
    {
        HashMap labels = new HashMap();
        List roots = new ArrayList();
        List components = getConnectedComponents(graph.getVariables(), labels);
        for(Iterator iterator = components.iterator(); iterator.hasNext();)
        {
            VariableCluster component = (VariableCluster)iterator.next();
            shuffle(component.getVariables());
            Variable v0 = null;
            Variable v1 = null;
            Iterator iterator1 = component.getVariables().iterator();
            while(iterator1.hasNext()) 
            {
                Variable _v0 = (Variable)iterator1.next();
                if(_v0.getNeighbors().size() == component.getVariables().size() - 1)
                    continue;
                for(Iterator iterator2 = component.getVariables().iterator(); iterator2.hasNext();)
                {
                    Variable _v1 = (Variable)iterator2.next();
                    if(_v0 != _v1 && !_v0.neighbors.contains(_v1))
                    {
                        v0 = _v0;
                        v1 = _v1;
                        break;
                    }
                }

                if(v0 != null)
                    break;
            }
            if(v0 == null)
            {
                roots.add(new VariableCluster(component.getVariables()));
            } else
            {
                VariableCluster root = splitRootCluster(component.getVariables(), labels, v0, v1);
                expand(root, labels);
                roots.add(root);
            }
        }

        return roots;
    }

    static List getConnectedComponents(List vertices, HashMap labels)
    {
        String firstLabel = "0";
        return splitUnlabeledComponents(vertices, labels, firstLabel);
    }

    static List splitUnlabeledComponents(List vertices, HashMap labels, String firstLabel)
    {
        ArrayList newLabels = new ArrayList();
        String newLabel = firstLabel;
        List openList = new ArrayList();
        for(Iterator iterator = vertices.iterator(); iterator.hasNext();)
        {
            Variable v = (Variable)iterator.next();
            if(labels.get(v) == null)
            {
                newLabels.add(newLabel);
                openList.clear();
                openList.add(v);
                while(openList.size() != 0) 
                {
                    Variable w = (Variable)openList.remove(openList.size() - 1);
                    labels.put(w, newLabel);
                    for(Iterator iterator2 = w.getNeighbors().iterator(); iterator2.hasNext();)
                    {
                        Variable neigh = (Variable)iterator2.next();
                        if(labels.get(neigh) == null)
                            openList.add(neigh);
                    }

                }
                newLabel = generateNewSiblingLabel(newLabel);
            }
        }

        ArrayList newClusters = new ArrayList(newLabels.size());
        VariableCluster newCluster;
        for(Iterator iterator1 = newLabels.iterator(); iterator1.hasNext(); newClusters.add(newCluster))
        {
            String label = (String)iterator1.next();
            ArrayList clusterVertices = new ArrayList();
            for(Iterator iterator3 = labels.keySet().iterator(); iterator3.hasNext();)
            {
                Variable v = (Variable)iterator3.next();
                if(((String)labels.get(v)).equals(label))
                    clusterVertices.add(v);
            }

            newCluster = new VariableCluster(clusterVertices);
        }

        return newClusters;
    }

    static VariableCluster splitRootCluster(List vertices, HashMap labels, Variable v0, Variable v1)
    {
        String TOP_LABEL = (String)labels.get(vertices.get(0));
        String LEFT_LABEL = generateNewChildLabel(TOP_LABEL);
        String MIDDLE_LABEL = generateNewSiblingLabel(LEFT_LABEL);
        String RIGHT_LABEL = generateNewSiblingLabel(MIDDLE_LABEL);
        Variable v;
        for(Iterator iterator = vertices.iterator(); iterator.hasNext(); labels.put(v, null))
            v = (Variable)iterator.next();

        labels.put(v0, LEFT_LABEL);
        labels.put(v1, RIGHT_LABEL);
        int i = 0;
        for(int lastIndexChanged = -1; lastIndexChanged != i; i = ++i % vertices.size())
        {
            Variable v = (Variable)vertices.get(i);
            if(labels.get(v) == null)
            {
                boolean hasNeighborLeft = false;
                boolean hasNeighborRight = false;
                for(Iterator iterator1 = v.neighbors.iterator(); iterator1.hasNext();)
                {
                    Variable neighbor = (Variable)iterator1.next();
                    String neighborLabel = (String)labels.get(neighbor);
                    if(RIGHT_LABEL.equals(neighborLabel))
                        hasNeighborRight = true;
                    else
                    if(LEFT_LABEL.equals(neighborLabel))
                        hasNeighborLeft = true;
                }

                if(hasNeighborLeft && hasNeighborRight)
                {
                    labels.put(v, TOP_LABEL);
                    lastIndexChanged = i;
                } else
                if(hasNeighborLeft)
                {
                    labels.put(v, LEFT_LABEL);
                    lastIndexChanged = i;
                } else
                if(hasNeighborRight)
                {
                    labels.put(v, RIGHT_LABEL);
                    lastIndexChanged = i;
                }
            }
        }

        VariableCluster left = new VariableCluster();
        VariableCluster middle = new VariableCluster();
        VariableCluster right = new VariableCluster();
        VariableCluster top = new VariableCluster();
        for(Iterator iterator2 = vertices.iterator(); iterator2.hasNext();)
        {
            Variable v = (Variable)iterator2.next();
            String label = (String)labels.get(v);
            if(label == null)
            {
                middle.getVariables().add(v);
                labels.put(v, MIDDLE_LABEL);
            } else
            if(label.equals(TOP_LABEL))
                top.getVariables().add(v);
            else
            if(label.equals(LEFT_LABEL))
                left.getVariables().add(v);
            else
            if(label.equals(RIGHT_LABEL))
                right.getVariables().add(v);
            else
                throw new IllegalArgumentException("GraphDivision.main() Error!");
        }

        top.addChild(left);
        if(middle.getVariables().size() > 0)
            top.addChild(middle);
        top.addChild(right);
        return top;
    }

    static void expand(VariableCluster root, HashMap labels)
    {
        LinkedList openList = new LinkedList();
        openList.addAll(root.getChildren());
        while(openList.size() > 0) 
        {
            VariableCluster cluster = (VariableCluster)openList.remove(0);
            List newSiblings = splitSiblings(cluster, labels);
            if(newSiblings != null)
            {
                openList.addAll(newSiblings);
            } else
            {
                VariableCluster newChild = splitChild(cluster, labels);
                if(newChild != null)
                    openList.add(newChild);
            }
        }
    }

    static List splitSiblings(VariableCluster cluster, HashMap labels)
    {
        VariableCluster parent = cluster.getParent();
        String firstLabel = (String)labels.get(cluster.getVariables().get(0));
        Variable v;
        for(Iterator iterator = cluster.getVariables().iterator(); iterator.hasNext(); labels.remove(v))
            v = (Variable)iterator.next();

        List newClusters = splitUnlabeledComponents(cluster.getVariables(), labels, firstLabel);
        if(newClusters.size() == 1)
            return null;
        parent.removeChild(cluster);
        VariableCluster newCluster;
        for(Iterator iterator1 = newClusters.iterator(); iterator1.hasNext(); parent.addChild(newCluster))
            newCluster = (VariableCluster)iterator1.next();

        return newClusters;
    }

    static VariableCluster splitChild(VariableCluster cluster, HashMap labels)
    {
        VariableCluster newChildCluster = new VariableCluster();
        String parentLabel = (String)labels.get(cluster.getParent().getVariables().get(0));
        String oldLabel = (String)labels.get(cluster.getVariables().get(0));
        String newLabel = generateNewChildLabel(oldLabel);
        ArrayList removedVertices = new ArrayList();
        for(Iterator iterator = cluster.getVariables().iterator(); iterator.hasNext();)
        {
            Variable v = (Variable)iterator.next();
            boolean hasNeighborInParentCluster = false;
            for(Iterator iterator2 = v.neighbors.iterator(); iterator2.hasNext();)
            {
                Variable neigh = (Variable)iterator2.next();
                if(((String)labels.get(neigh)).equals(parentLabel))
                {
                    hasNeighborInParentCluster = true;
                    break;
                }
            }

            if(!hasNeighborInParentCluster)
            {
                labels.put(v, newLabel);
                newChildCluster.getVariables().add(v);
                removedVertices.add(v);
            }
        }

        Variable v;
        for(Iterator iterator1 = removedVertices.iterator(); iterator1.hasNext(); cluster.getVariables().remove(v))
            v = (Variable)iterator1.next();

        if(newChildCluster.getVariables().size() > 0)
        {
            cluster.addChild(newChildCluster);
            return newChildCluster;
        } else
        {
            return null;
        }
    }

    static int calculateSize(List roots, ArrayOfLists domains)
    {
        List openList = new ArrayList();
        openList.addAll(roots);
        int totalSize = 0;
        while(openList.size() > 0) 
        {
            VariableCluster node = (VariableCluster)openList.remove(openList.size() - 1);
            totalSize += calculateClusterSize(node, domains);
            if(node.hasChildren())
            {
                TreeNode ch;
                for(Iterator iterator = node.getChildren().iterator(); iterator.hasNext(); openList.add((VariableCluster)ch))
                    ch = (TreeNode)iterator.next();

            }
        }
        return totalSize;
    }

    static int calculateClusterSize(VariableCluster cluster, ArrayOfLists domains)
    {
        int clusterSize = 1;
        List branchVariables = new ArrayList();
        cluster.getBranchLabels(branchVariables);
        for(int i = 0; i < branchVariables.size(); i++)
            if(branchVariables.get(i) != null)
            {
                for(int j = 0; j < ((List)branchVariables.get(i)).size(); j++)
                {
                    int varID = ((Variable)((List)branchVariables.get(i)).get(j)).getId();
                    int varSize = domains.get(varID).size();
                    if(varSize != 0)
                        clusterSize *= varSize;
                }

            }

        return clusterSize;
    }

    public static void shuffle(List list)
    {
        Object permutation[] = new Object[list.size()];
        for(int i = 0; i < list.size(); i++)
        {
            int r;
            do
                r = random.nextInt(list.size());
            while(permutation[r] != null);
            permutation[r] = list.get(i);
        }

        list.clear();
        for(int i = 0; i < permutation.length; i++)
            list.add(permutation[i]);

    }

    static String generateNewSiblingLabel(String label)
    {
        String vals[] = label.split("_");
        String lastDigit = vals[vals.length - 1];
        int x = Integer.parseInt(lastDigit);
        x++;
        String newLabel = label.substring(0, label.length() - lastDigit.length());
        newLabel = (new StringBuilder(String.valueOf(newLabel))).append(x).toString();
        return newLabel;
    }

    static String generateNewChildLabel(String label)
    {
        String newLabel = (new StringBuilder(String.valueOf(label))).append("_").append(0).toString();
        return newLabel;
    }

    static final int NUM_TRIES = 5;
    static Random random = new Random();

}
