package cs455.overlay.util;

public class Link {
    private String src = null;
    private String dest = null;
    private int cost = 0;

    public Link(String src, String dest, int cost)
    {
        this.src = src;
        this.dest = dest;
        this.cost = cost;
        System.out.println("Created link between " + src + " and " + dest + " with cost " + String.valueOf(cost));
    }

    public String getSrc()
    {
        return src;
    }

    public String getDest()
    {
        return dest;
    }

    public int getCost()
    {
        return cost;
    }
}
