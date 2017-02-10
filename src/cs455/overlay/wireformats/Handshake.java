package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handshake implements Event{
    private String IP = null;
    private int port = 0;
    private Socket socket = null;

    Handshake(String IPAddr, int portnum)
    {
        IP = IPAddr;
        port = portnum;
    }

    Handshake(String IPAddr, int portnum, Socket socket)
    {
        IP = IPAddr;
        port = portnum;
        this.socket = socket;
    }

    // Format: int Message_Type, int len_IP, char[] IP, int port_num
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.HANDSHAKE.getValue());

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

    @Override
    public Protocol getType() {
        return Protocol.HANDSHAKE;
    }

    public String getIP()
    {
        return IP;
    }

    public int getPort()
    {
        return port;
    }

    public Socket getSocket()
    {
        return socket;
    }
}
