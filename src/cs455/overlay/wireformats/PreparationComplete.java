package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PreparationComplete implements Event{

    private String IP = null;
    private int port = 0;

    public PreparationComplete(String IPAddr, int portnum)
    {
        IP = IPAddr;
        port = portnum;
    }

    @Override
    public Protocol getType() {
        return Protocol.PREPARATION_COMPLETE;
    }

    // Format: int Message_Type, int len_IP, char[] IP, int port_num
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.PREPARATION_COMPLETE.getValue());

        // Write IP Length
        dout.writeInt(IP.length());

        // Write IP
        byte[] IPBytes = IP.getBytes();
        dout.write(IPBytes);

        // Write Port Number
        dout.writeInt(port);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public String getIP()
    {
        return IP;
    }

    public int getPort()
    {
        return port;
    }
}
