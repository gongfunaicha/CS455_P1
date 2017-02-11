package cs455.overlay.util;

import cs455.overlay.wireformats.TrafficSummary;

import java.util.ArrayList;

public class StatisticsCollectorAndDisplay {
    private ArrayList<TrafficSummary> trafficSummaryArrayList = null;
    private long sendCounter = 0;
    private long receiveCounter = 0;
    private long sendSum = 0;
    private long receiveSum = 0;
    private int numNodes = 0;

    public StatisticsCollectorAndDisplay(int numNodes)
    {
        trafficSummaryArrayList = new ArrayList<>();
        sendCounter = 0;
        receiveCounter = 0;
        sendSum = 0;
        receiveSum = 0;
        this.numNodes = numNodes;
    }

    public synchronized void addTrafficSummary(TrafficSummary ts)
    {
        trafficSummaryArrayList.add(ts);
        sendCounter += ts.getSendTracker();
        receiveCounter += ts.getReceiveTracker();
        sendSum += ts.getSendSummation();
        receiveSum += ts.getReceiveSummation();

        // Check whether all traffic summary collected
        if (trafficSummaryArrayList.size() == numNodes)
        {
            // All collected, display summary
            displaySummary();
        }
    }

    private void displaySummary()
    {
        System.out.println("Node\tNumber Sent\tNumber Received\tSummation Sent\tSummation Received\tNumber Relayed");
        for (TrafficSummary ts: trafficSummaryArrayList)
        {
            String output = "";
            output += ts.getIdentity();
            output += "\t";
            output += String.valueOf(ts.getSendTracker());
            output += "\t";
            output += String.valueOf(ts.getReceiveTracker());
            output += "\t";
            output += String.valueOf(ts.getSendSummation());
            output += "\t";
            output += String.valueOf(ts.getReceiveSummation());
            output += "\t";
            output += String.valueOf(ts.getRelayTracker());
            System.out.println(output);
        }
        String lastline = "Sum\t";
        lastline += String.valueOf(sendCounter);
        lastline += "\t";
        lastline += String.valueOf(receiveCounter);
        lastline += "\t";
        lastline += String.valueOf(sendSum);
        lastline += "\t";
        lastline += String.valueOf(receiveSum);
        System.out.println(lastline);
    }

}
