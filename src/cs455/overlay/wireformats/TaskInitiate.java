package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskInitiate implements Event{

    private int numRounds = 0;

    public TaskInitiate(int numRounds)
    {
        this.numRounds = numRounds;
    }

    @Override
    public Protocol getType() {
        return Protocol.TASK_INITIATE;
    }

    // Format: int type, int numRounds
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.TASK_INITIATE.getValue());

        // Write numRounds
        dout.writeInt(numRounds);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public int getNumRounds()
    {
        return numRounds;
    }
}
