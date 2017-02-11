package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummary implements Event{
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;
    private String identity = null;

    public TrafficSummary(int sendTracker, int receiveTracker, int relayTracker, long sendSummation, long receiveSummation, String identity)
    {
        this.sendTracker = sendTracker;
        this.receiveTracker = receiveTracker;
        this.relayTracker = relayTracker;
        this.sendSummation = sendSummation;
        this.receiveSummation = receiveSummation;
        this.identity = identity;
    }

    // Format: int type, int len_identity, String identity, int sendTracker, int receiveTracker, int relayTracker, long sendSummation, long receiveSummation
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.TRAFFIC_SUMMARY.getValue());

        // Write len_identity
        dout.writeInt(identity.length());

        // Write identity
        dout.write(identity.getBytes());

        // Write sendTracker
        dout.writeInt(sendTracker);

        // Write receiveTracker
        dout.writeInt(receiveTracker);

        // Write relayTracker
        dout.writeInt(relayTracker);

        // Write sendSummation
        dout.writeLong(sendSummation);

        // Write receiveSummation
        dout.writeLong(receiveSummation);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public Protocol getType() {
        return Protocol.TRAFFIC_SUMMARY;
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

    public String getIdentity()
    {
        return identity;
    }
}
