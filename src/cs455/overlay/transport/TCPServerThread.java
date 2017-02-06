package cs455.overlay.transport;


import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class TCPServerThread extends Thread {

    private ServerSocket serverSocket = null;
    private String HostIP = null;
    private int port = 0;
    private Node node;

    public TCPServerThread(Node nd, int portnum)
    {
        this.node = nd;
        this.port = portnum;
    }

    public void run()
    {
        // Get current host IP
        try {
            HostIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Unable to obtain host address.");
            HostIP = "Unknown";
        }
        // Try to bind to specified port
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Unable to bind to port. Program will now exit.");
            System.exit(1);
        }

        // assign port to the real port number
        port = serverSocket.getLocalPort();

        System.out.println("Node is now listening on IP: " + HostIP + " Port: " + port);
        //TODO: Accept incoming connections and handle them
    }
}
