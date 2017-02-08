package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DeregisterRequest implements Event{

    private String IP = null;
    private int port = 0;
    private Socket requesterSocket = null;

    public DeregisterRequest(String IPAddr, int portnum)
    {
        this.IP = IPAddr;
        this.port = portnum;
    }

    public DeregisterRequest(String IPAddr, int portnum, Socket socket)
    {
        this.IP = IPAddr;
        this.port = portnum;
        this.requesterSocket = socket;
    }

    @Override
    public Protocol getType() {
        return Protocol.DEREGISTER_REQUEST;
    }

    // Format: int Message_Type, int len_IP, char[] IP, int port_num
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.DEREGISTER_REQUEST.getValue());

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

    public Socket getRequesterSocket()
    {
        return requesterSocket;
    }
}
