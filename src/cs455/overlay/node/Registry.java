package cs455.overlay.node;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

public class Registry implements Node {

    private TCPServerThread registryServerThread = null;

    public Registry(int portnum)
    {
        // Start registry server thread
        startRegistryServerThread(portnum);

        System.out.println("Registry is now exiting.");
    }

    public static void main(String[] args)
    {
        // Command line check
        if (args.length == 1)
        {
            int port = 0;
            try
            {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Error: Specified port number must be an integer. Program will now exit.");
                System.exit(1);
            }
            if (port < 0 || port > 65535)
            {
                System.out.println("Error: Specified port number must be between 0 and 65535. Program will now exit.");
                System.exit(1);
            }
            // Check passed through, create registry instance
            Registry registry = new Registry(port);
            // TODO: Registry next steps
        }
        else
        {
            System.out.println("Invalid number of arguments");
            System.out.println("Usage: java cs455.overlay.node.Registry portnum");
        }
    }

    @Override
    public void onEvent(Event e) {
        //TODO: What to do on onEvent?
    }

    private void startRegistryServerThread(int portnum)
    {
        registryServerThread = new TCPServerThread(this, portnum);
        registryServerThread.start();
    }
}
