package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// EventFactory is responsible of creating instance of subclass of Event based on the data it received
public class EventFactory {

    public static Event createEventFromData(byte[] data, Socket socket) throws IOException
    {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int numtype = din.readInt();
        Protocol type = Protocol.valueOf(numtype);

        switch (type)
        {
            case REGISTER_REQUEST:
                // remaining format: int len_IP, char[] IP, int port_num
                int len_IP = din.readInt();
                byte[] IPBytes = new byte[len_IP];
                din.readFully(IPBytes,0,len_IP);
                String IP = new String(IPBytes);
                int port = din.readInt();
                // Return RegisterRequest instance
                return new RegisterRequest(IP, port, socket);
            default:
                System.out.println("Invalid message type received.");
                return null;
        }
    }

}
