package cs455.overlay.dijkstra;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.util.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// NodeAndLink is used to store nodes and links, and do dijkstra
public class NodeAndLink {

    private int numNodes = 0;
    private ArrayList<String> Nodes = null;
    private int numLinks = 0;
    private ArrayList<Link> linkInfo = null;
    private String currentNode = null;
    private MessagingNode node = null;

    public NodeAndLink(int numNodes, String stringNodes, int numLinks, String linkInfo, String currentNode, MessagingNode node)
    {
        this.numNodes = numNodes;
        this.numLinks = numLinks;
        this.currentNode = currentNode;

        // Decode stringNodes
        this.Nodes = new ArrayList<>();
        String[] splittedNodes = stringNodes.split("\\n");
        for (String Node: splittedNodes)
        {
            Nodes.add(Node);
        }

        // Decode linkInfo
        this.linkInfo = new ArrayList<>();
        String[] splittedLinks = linkInfo.split("\\n");
        for (String Link: splittedLinks)
        {
            // Separate src, dest, and cost
            String[] splitted = Link.split(" ");
            Link link = new Link(splitted[0],splitted[1],Integer.valueOf(splitted[2]), false);
            this.linkInfo.add(link);
        }
        this.node = node;
    }

    public RoutingCache Dijkstra()
    {
        // Initialize all data structures
        HashMap<String, String> lastNodeBeforeArrival = new HashMap<>();
        HashMap<String, Integer> lastCost = new HashMap<>();
        HashMap<String, String> nodeRoute = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> adjTable = new HashMap<>();
        HashMap<String, Integer> currentNodeCost = new HashMap<>(); // Not yet final
        HashMap<String, Integer> finalCost = new HashMap<>(); // Only final
        HashMap<String, String> nextHop = new HashMap<>();
        ArrayList<String> nodesNeedToContact = new ArrayList<>();

        for (String Node: this.Nodes)
        {
            if (Node.equals(currentNode))
            {
                // Current Node
                nodeRoute.put(Node, Node);
                finalCost.put(Node, 0);
                nextHop.put(Node, Node);
            }
            else
            {
                // Other Node
                currentNodeCost.put(Node, -1);
            }

            // Initialize adjTable
            adjTable.put(Node, new HashMap<>());
        }

        for (Link link: linkInfo)
        {
            String src = link.getSrc();
            String dest = link.getDest();
            int cost = link.getCost();

            adjTable.get(src).put(dest, cost);
            adjTable.get(dest).put(src, cost);

            if (src.equals(currentNode))
            {
                // Need to add to nodes need to contact
                nodesNeedToContact.add(dest);
            }
        }

        // Update cost directly connected to current node
        for (Map.Entry<String, Integer> entry: adjTable.get(currentNode).entrySet())
        {
            String dest = entry.getKey();
            int cost = entry.getValue();
            lastNodeBeforeArrival.put(dest, currentNode);
            lastCost.put(dest, cost);
            currentNodeCost.put(dest, cost);
            // Directly connected, nexthop is itself
            nextHop.put(dest, dest);
        }

        // Start dijkstra
        while (currentNodeCost.size() != 0)
        {
            // Find the node with smallest cost
            String smallestNode = findNodeWithSmallestCost(currentNodeCost);

            // Get cost and move node into finalCost
            int cost = currentNodeCost.get(smallestNode);
            currentNodeCost.remove(smallestNode);
            finalCost.put(smallestNode, cost);

            // Update data structures
            String previousNode = lastNodeBeforeArrival.get(smallestNode);
            int linkCost = lastCost.get(smallestNode);
            nodeRoute.put(smallestNode, nodeRoute.get(previousNode) + "--" + String.valueOf(linkCost) + "--" + smallestNode);

            // Update currentNodeCost
            for (Map.Entry<String, Integer> entry: adjTable.get(smallestNode).entrySet())
            {
                String dest = entry.getKey();
                int costOfLink = entry.getValue();
                if (currentNodeCost.containsKey(dest))
                {
                    int originalCost = currentNodeCost.get(dest);
                    // If dest already final, disregard
                    if ((originalCost == -1) || ((cost + costOfLink) < originalCost))
                    {
                        // Update cost with new cost
                        lastNodeBeforeArrival.put(dest, smallestNode);
                        lastCost.put(dest, costOfLink);
                        currentNodeCost.put(dest, cost + costOfLink);
                        nextHop.put(dest, nextHop.get(smallestNode));
                    }
                }
            }
        }

        // Finished dijkstra, create routing cache instance and return
        return new RoutingCache(nodeRoute, finalCost, nextHop, nodesNeedToContact, node);
    }

    private String findNodeWithSmallestCost(HashMap<String, Integer> currentNodeCost)
    {
        int minCost = -1;
        String minNode = null;
        for (Map.Entry<String, Integer> entry: currentNodeCost.entrySet())
        {
            String node = entry.getKey();
            int cost = entry.getValue();
            if (cost == -1)
            {
                // If cost == -1, it means no route to node
                continue;
            }
            if ((minCost == -1) || (cost < minCost))
            {
                // If smaller than current min cost, update min cost
                minCost = cost;
                minNode = node;
            }
        }
        return minNode;
    }
}
