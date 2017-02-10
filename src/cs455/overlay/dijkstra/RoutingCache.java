package cs455.overlay.dijkstra;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoutingCache {
    private HashMap<String, String> nodeRoute = null;
    private HashMap<String, Integer> finalCost = null;
    private HashMap<String, String> nextHop = null;
    private ArrayList<String> nodesNeedToContact = null;
    private HashMap<String, TCPSender> senders = null;
    private MessagingNode node = null;

    RoutingCache(HashMap<String, String> nodeRoute, HashMap<String, Integer> finalCost, HashMap<String, String> nextHop, ArrayList<String> nodesNeedToContact, MessagingNode node)
    {
        this.nodeRoute = nodeRoute;
        this.finalCost = finalCost;
        this.nextHop = nextHop;
        this.nodesNeedToContact = nodesNeedToContact;
        this.senders = new HashMap<>();
        this.node = node;
    }

    public String getNextHop(String dest)
    {
        return nextHop.get(dest);
    }

    public ArrayList<String> getNodesNeedToContact()
    {
        return nodesNeedToContact;
    }

    public TCPSender getSender(String dest)
    {
        return senders.get(dest);
    }

    public synchronized void setSender(String dest, TCPSender sender)
    {
        senders.put(dest, sender);
        if (senders.size() == 4)
        {
            node.sendPreparationComplete();
        }
    }

    public String getShortestPaths()
    {
        String shortestPath = "";
        for (String route: this.nodeRoute.values())
        {
            if (!shortestPath.equals(""))
            {
                shortestPath += "\n";
            }
            shortestPath += route;
        }
        return shortestPath;
    }

    public ArrayList<String> getOtherNodes()
    {
        ArrayList<String> otherNodes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: finalCost.entrySet())
        {
            if (entry.getValue() != 0)
            {
                // Cost 0 => current node
                otherNodes.add(entry.getKey());
            }
        }
        return otherNodes;
    }
}
