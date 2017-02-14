package cs455.overlay.util;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class OverlayCreator {
    private ArrayList<Link> links = null;
    private ArrayList<String> Nodes = null;
    private int numNodes = 0;
    private int numLinks = 0;

    public OverlayCreator(Set<String> Nodes)
    {
        this.links = new ArrayList<>();
        this.Nodes = new ArrayList<>();
        this.Nodes.addAll(Nodes);
        this.numNodes = this.Nodes.size();
    }

    // Return value indicates whether it is successful
    public void createOverlay()
    {
        System.out.println("Starting to create overlay...");
        System.out.println("Number of nodes in overlay: " + String.valueOf(numNodes));

        // Initialize random number generator
        Random random = new Random();

        // First connect adjacent nodes
        for (int i = 0; i < numNodes; i++)
        {
            Link link = new Link(Nodes.get(i % numNodes), Nodes.get((i+1) % numNodes), random.nextInt(10) + 1, true);
            links.add(link);
        }

        // Next connect nodes adjacent by 1
        for (int i = 0; i < numNodes; i++)
        {
            Link link = new Link(Nodes.get(i % numNodes), Nodes.get((i+2) % numNodes), random.nextInt(10) + 1, true);
            links.add(link);
        }

        this.numLinks = links.size();

        System.out.println("Overlay creation complete. Number of links: " + String.valueOf(this.numLinks));
        System.out.println("Please wait at least 5 seconds before issuing send-overlay-link-weights due to messaging nodes are connecting to each other.");
    }

    // Return formatted links
    public String formattedOverlay()
    {
        String output = "";
        for (Link link: links)
        {
            if (!output.equals(""))
            {
                // Not the first link, add "\n"
                output += "\n";
            }
            output += link.getSrc() + " " + link.getDest() + " " + String.valueOf(link.getCost());
        }
        return output;
    }

    public int getNumLinks()
    {
        return numLinks;
    }

    public int getNumNodes()
    {
        return numNodes;
    }

    public ArrayList<String> getNeedToConnect(String src)
    {
        ArrayList<String> nodesNeedToConnect = new ArrayList<>();
        for (Link link: links)
        {
            if (link.getSrc().equals(src))
            {
                nodesNeedToConnect.add(link.getDest());
            }
        }
        return nodesNeedToConnect;
    }

}
