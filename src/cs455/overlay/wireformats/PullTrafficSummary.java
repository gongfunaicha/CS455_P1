package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PullTrafficSummary implements Event{

    public PullTrafficSummary()
    {

    }

    @Override
    public Protocol getType() {
        return Protocol.PULL_TRAFFIC_SUMMARY;
    }

    // Format: int Message_Type
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.PULL_TRAFFIC_SUMMARY.getValue());

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
