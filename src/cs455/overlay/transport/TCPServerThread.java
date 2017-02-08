package cs455.overlay.transport;


import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TCPServerThread extends Thread {

    private ServerSocket serverSocket = null;
    private String HostIP = null;
    private int port = 0;
    private Node node = null;
    private ArrayList<TCPReceiverThread> listTCPReceiverThread = null;

    public TCPServerThread(Node nd, int portnum)
    {
        this.node = nd;
        this.port = portnum;
        this.listTCPReceiverThread = new ArrayList<>();
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

        // Continuously accept incoming connection and spawn TCP Receiver Thread
        while (!isInterrupted())
        {
            Socket incoming_socket;
            TCPReceiverThread tcpReceiverThread;
            try {
                incoming_socket = serverSocket.accept();
                tcpReceiverThread = new TCPReceiverThread(incoming_socket, node);
            } catch (IOException e) {
                System.out.println("Failed to accept incoming connection.");
                continue;
            }
            // Start TCP Receiver Thread and add to list
            tcpReceiverThread.run();
            listTCPReceiverThread.add(tcpReceiverThread);
        }

        // Close all sockets ???
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Failed to close server socket.");
        }

        for (TCPReceiverThread tcpReceiverThread : listTCPReceiverThread)
        {
            Socket connected_socket = tcpReceiverThread.getSocket();
            try {
                connected_socket.close();
            } catch (IOException e) {
                System.out.println("Failed to close connected socket.");
            }
        }

        System.out.println("TCP Server Thread has stopped.");
    }

    public String getHostIP()
    {
        return HostIP;
    }

    public int getPort()
    {
        return port;
    }

    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }

}
