package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread extends Thread{
    private Socket socket;
    private DataInputStream din;
    private Node node;

    public TCPReceiverThread(Socket socket, Node nd) throws IOException
    {
        this.socket = socket;
        this.din = new DataInputStream(socket.getInputStream());
        this.node = nd;
    }

    @Override
    public void run() {
        int dataLength;
        byte[] data;
        while (socket != null) {
            // Read from socket
            try {
                dataLength = din.readInt();
                data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
            } catch (SocketException se) {
                System.out.println(se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                break;
            }

            // Notify node of event
            Event event;
            try {
                event = EventFactory.createEventFromData(data, socket);
                node.onEvent(event);
            } catch (IOException e) {
                System.out.println("Received invalid data. Data dropped.");
            }
        }
    }

    public Socket getSocket()
    {
        return socket;
    }
}
