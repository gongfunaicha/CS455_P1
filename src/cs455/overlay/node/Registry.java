package cs455.overlay.node;
import cs455.overlay.transport.TCPServerThread;

public class Registry {

    public static void main(String[] args)
    {
        TCPServerThread tcpServerThread = new TCPServerThread();
        tcpServerThread.start();
        System.out.println("Program is now exiting.");
    }
}
