package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class TCPServerThread extends Thread {

    private ServerSocket serverSocket = null;
    private String HostIP = null;

    public void run()
    {
        // Get current host IP
        try {
            HostIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.print("Unable to obtain host address.");
            HostIP = "Unknown";
        }
        boolean success = false;
        short port = 30000;
        // Try to bind to port until success
        while (!success)
        {
            try
            {
                success = true;
                serverSocket = new ServerSocket(port, 50);
            }
            catch (IOException IOE)
            {
                success = false;
                port += 1;
            }
        }
        System.out.println("Registry is now running on IP: " + HostIP + " Port: " + String.valueOf(port));
    }

}
