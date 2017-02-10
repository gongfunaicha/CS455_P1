package cs455.overlay.dijkstra;

import cs455.overlay.transport.TCPSender;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutingCache {
    private HashMap<String, String> nodeRoute = null;
    private HashMap<String, Integer> finalCost = null;
    private HashMap<String, String> nextHop = null;
    private ArrayList<String> nodesNeedToContact = null;
    private HashMap<String, TCPSender> senders = null;

    RoutingCache(HashMap<String, String> nodeRoute, HashMap<String, Integer> finalCost, HashMap<String, String> nextHop, ArrayList<String> nodesNeedToContact)
    {
        this.nodeRoute = nodeRoute;
        this.finalCost = finalCost;
        this.nextHop = nextHop;
        this.nodesNeedToContact = nodesNeedToContact;
        this.senders = new HashMap<>();
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

    public synchronized  void setSender(String dest, TCPSender sender)
    {
        senders.put(dest, sender);
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
}
