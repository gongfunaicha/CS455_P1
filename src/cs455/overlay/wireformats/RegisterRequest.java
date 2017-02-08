package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegisterRequest implements Event{

    private String IP = null;
    private int port = 0;
    private Socket requesterSocket = null;

    public RegisterRequest(String IPAddr, int portnum)
    {
        this.IP = IPAddr;
        this.port = portnum;
    }

    public RegisterRequest(String IPAddr, int portnum, Socket requesterSkt)
    {
        this.IP = IPAddr;
        this.port = portnum;
        this.requesterSocket = requesterSkt;
    }

    @Override
    public Protocol getType() {
        return Protocol.REGISTER_REQUEST;
    }

    // getBytes is used to get byte[] to send
    // Format: int Message_Type, int len_IP, char[] IP, int port_num
    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.REGISTER_REQUEST.getValue());

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

    public Socket getRequesterSocket()
    {
        return requesterSocket;
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
