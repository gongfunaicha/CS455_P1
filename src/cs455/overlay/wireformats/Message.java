package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message implements Event{

    private String srcIdentity = null;
    private String destIdentity = null;
    private int payload = 0;

    public Message(String src, String dest, int payload)
    {
        srcIdentity = src;
        destIdentity = dest;
        this.payload = payload;
    }

    // Format: int type, int len_srcIdentity, String srcIdentity, int len_destIdentity, String destIdentity, int payload
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.MESSAGE.getValue());

        // Write len_srcIdentity
        dout.writeInt(srcIdentity.length());

        // Write srcIdentity
        dout.write(srcIdentity.getBytes());

        // Write len_destIdentity
        dout.writeInt(destIdentity.length());

        // Write destIdentity
        dout.write(destIdentity.getBytes());

        // Write payload
        dout.writeInt(payload);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public Protocol getType() {
        return Protocol.MESSAGE;
    }

    public String getSrcIdentity()
    {
        return srcIdentity;
    }

    public String getDestIdentity()
    {
        return  destIdentity;
    }

    public int getPayload()
    {
        return payload;
    }
}
