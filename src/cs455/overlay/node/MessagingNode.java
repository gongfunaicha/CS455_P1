package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterRequest;
import cs455.overlay.wireformats.RegisterResponse;

import java.io.IOException;
import java.net.Socket;

public class MessagingNode implements Node {

    private String registry_host;
    private int registry_port;
    private TCPServerThread messagingNodeServerThread = null;
    private TCPSender registrySender = null;
    private boolean shortestPathCalculated = false;

    @Override
    public void onEvent(Event e) {
        Protocol type = e.getType();
        switch (type)
        {
            case REGISTER_RESPONSE:
                handleRegisterResponse(e);
                break;
            default:
                System.out.println("Invalid event received.");
        }
    }

    public MessagingNode(String[] args)
    {
        // Check arguments, exit on fail
        checkArguments(args);

        // Start messaging node server thread
        startMessagingNodeServerThread();

        // Connect registry
        connectRegistry();

        // Send register request
        sendRegisterRequest();

        while (true)
        {
            String userinput = System.console().readLine();
            if (userinput.equals("print-shortest-path"))
            {
                // TODO: handle user input of "print-shortest-path"
            }
            else if (userinput.equals("exit-overlay"))
            {
                // TODO: handle user input of "exit-overlay"
            }
            else
            {
                System.out.println("Unrecognized command. Please enter:");
                System.out.println("print-shortest-path");
                System.out.println("or");
                System.out.println("exit-overlay");
            }
        }

    }

    public static void main(String[] args)
    {
        // Command line check
        if (args.length != 2)
        {
            // Wrong number of arguments, display error message
            System.out.println("Invalid number of arguments");
            System.out.println("Usage: java cs455.overlay.node.MessagingNode registry-host registry-port");
            System.exit(1);
        }

        // Right number of arguments, create instance of Messaging Node
        MessagingNode msgnode = new MessagingNode(args);


    }

    private boolean validateIP(String IP)
    {
        // Cut based on "."
        String[] splitted = IP.split("\\.");

        // If not four parts, not a valid IP
        if (splitted.length != 4)
            return false;

        for (String substr: splitted)
        {
            int number = 0;
            try
            {
                number = Integer.parseInt(substr);
            }
            catch (NumberFormatException e)
            {
                // Not valid integer, not valid IP
                return false;
            }
            // Not between 0 and 255, not valid IP
            if (number < 0 || number > 255)
                return false;
        }

        // All passed, is valid IP
        return true;
    }

    private void checkArguments(String[] args)
    {
        // Check hostname
        if (!validateIP(args[0]))
        {
            System.out.println("Error: Inputted IP not valid. Please check the registry-host. Program will now exit.");
            System.exit(1);
        }

        registry_host = args[0];

        // Check port number
        try
        {
            registry_port = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error: Inputted port number not int. Please check the registry-port. Program will now exit.");
            System.exit(1);
        }

        // Check port number between 0 and 65535
        if (registry_port < 0 || registry_port > 65535)
        {
            System.out.println("Error: Invalid port number. Port number should be between 0 and 65535. Program will now exit.");
            System.exit(1);
        }
    }

    private void startMessagingNodeServerThread()
    {
        // Set portnum = 0 to make java select an available port freely
        messagingNodeServerThread = new TCPServerThread(this, 0);
        messagingNodeServerThread.start();
    }

    private void connectRegistry()
    {
        // Connect to registry and create registrySender
        try {
            Socket registrySocket = new Socket(registry_host, registry_port);
            registrySender = new TCPSender(registrySocket);
        } catch (IOException e) {
            System.out.println("Unable to connect to registry. Program will now exit");
            System.exit(1);
        }
    }

    private void sendRegisterRequest()
    {
        // Send register request to the registry
        RegisterRequest registerRequest = new RegisterRequest(messagingNodeServerThread.getHostIP(), messagingNodeServerThread.getPort());
        try {
            registrySender.sendData(registerRequest.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("Unable to send register request to registry. Program will now exit");
            System.exit(1);
        }
    }

    private void handleRegisterResponse(Event e)
    {
        RegisterResponse registerResponse = (RegisterResponse)e;
        boolean status = registerResponse.getStatus();
        String addiInfo = registerResponse.getAddiinfo();
        if (status == false)
        {
            // Register response is failure
            System.out.println("Received failure register response from registry with following information:");
            System.out.println(addiInfo);
            System.out.println("Program will now exit.");
            System.exit(1);
        }
        else
        {
            // Register success
            System.out.println("Successfully registered with registry. The registry sent the following information:");
            System.out.println(addiInfo);
        }
    }
}
