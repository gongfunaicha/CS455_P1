package cs455.overlay.transport;

import cs455.overlay.node.Registry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class TCPServerThread extends Thread {

    private ServerSocket serverSocket = null;
    private String HostIP = null;
    private int port = 0;
    private Registry registry;

    public TCPServerThread(Registry reg)
    {
        this.registry = reg;
    }

    public void run()
    {
        // Get current host IP
        try {
            HostIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.print("Unable to obtain host address.");
            HostIP = "Unknown";
        }
        // Try to bind to an available port
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("Unable to bind to an available port. Program will now exit.");
            System.exit(1);
        }

        // assign port to the real port number
        port = serverSocket.getLocalPort();

        System.out.println("Registry is now running on IP: " + HostIP + " Port: " + port);
        //TODO: Accept incoming connections and handle them
    }
}
