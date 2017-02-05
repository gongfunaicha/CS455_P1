package cs455.overlay.node;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

public class Registry implements Node {

    public Registry()
    {
        TCPServerThread tcpServerThread = new TCPServerThread(this);
        tcpServerThread.start();
        System.out.println("Program is now exiting.");
    }

    public static void main(String[] args)
    {
        Registry registry = new Registry();
    }

    @Override
    public void onEvent(Event e) {
        //TODO: What to do on onEvent?
    }
}
