package cs455.overlay.util;

public class CommunicationTracker {
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;

    public CommunicationTracker()
    {
        sendTracker = 0;
        receiveTracker = 0;
        relayTracker = 0;
        sendSummation = 0;
        receiveSummation = 0;
    }

    public synchronized void incrementSendTracker()
    {
        sendTracker++;
    }

    public synchronized void incrementReceiveTracker()
    {
        receiveTracker++;
    }

    public synchronized void incrementRelayTracker()
    {
        relayTracker++;
    }

    public synchronized void addSendSummation(int num)
    {
        sendSummation += num;
    }

    public synchronized void addReceiveSummation(int num)
    {
        receiveSummation += num;
    }

    public int getSendTracker()
    {
        return sendTracker;
    }

    public int getReceiveTracker()
    {
        return receiveTracker;
    }

    public int getRelayTracker()
    {
        return relayTracker;
    }

    public long getSendSummation()
    {
        return sendSummation;
    }

    public long getReceiveSummation()
    {
        return receiveSummation;
    }
}
