// Currently not in use
package cs455.overlay.transport;

import cs455.overlay.node.MessagingNode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class MessagingNodeServerThread extends Thread{
    private ServerSocket serverSocket = null;
    private String HostIP = null;
    private int port = 0;
    private MessagingNode messagingNode = null;

    public MessagingNodeServerThread(MessagingNode msgNode)
    {
        this.messagingNode = msgNode;
    }

    @Override
    public void run() {
        // Get current host IP
        try {
            HostIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Unable to obtain host address. Program will now exit");
            System.exit(1);
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

        System.out.println("Messaging node is now listening on IP: " + HostIP + " Port: " + port);
        //TODO: Accept incoming connections and handle them
    }
}
