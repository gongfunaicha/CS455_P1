package cs455.overlay.node;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Registry implements Node {

    private TCPServerThread registryServerThread = null;
    private HashMap<Socket, TCPSender> registrySenders = null;
    private HashMap<String, TCPSender> registeredNodes = null;

    public Registry(int portnum)
    {
        // Start registry server thread
        startRegistryServerThread(portnum);

        registrySenders = new HashMap<>();
        registeredNodes = new HashMap<>();

        // TODO: Registry next steps
//         For testing
        while (true)
        {
            int i = 0;
            i=i+1;
        }
//        System.out.println("Registry is now exiting.");
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
        }
        else
        {
            System.out.println("Invalid number of arguments");
            System.out.println("Usage: java cs455.overlay.node.Registry portnum");
        }
    }

    @Override
    public void onEvent(Event e) {
        Protocol type = e.getType();
        switch (type)
        {
            case REGISTER_REQUEST:
                handleRegisterRequest(e);
                break;
            case DEREGISTER_REQUEST:
                handleDeregisterRequest(e);
                break;
            default:
                System.out.println("Invalid event received.");
        }
    }

    private void startRegistryServerThread(int portnum)
    {
        registryServerThread = new TCPServerThread(this, portnum);
        registryServerThread.start();
    }

    private void handleRegisterRequest(Event e)
    {
        RegisterRequest registerRequest = (RegisterRequest)e;
        Socket requesterSocket = registerRequest.getRequesterSocket();
        String IP = registerRequest.getIP();
        int port = registerRequest.getPort();
        System.out.println("Got register request from IP: " + IP + " Port: " + String.valueOf(port) + ".");

        // Duplicate entry, send failure response
        if (registrySenders.containsKey(requesterSocket))
        {
            sendRegisterResponse(registrySenders.get(requesterSocket),false,"Node had previously registered.");
            return;
        }

        try {
            String full_identity = IP + ":" + port;
            // First check whether it has registered
            if (registeredNodes.containsKey(full_identity))
            {
                sendRegisterResponse(registeredNodes.get(full_identity),false,"Node had previously registered.");
                return;
            }

            // Contains no entry in registrySenders and registeredNodes, create TCPSender instance
            TCPSender sender = new TCPSender(requesterSocket);

            // Check whether it is honest, if 127.0.0.1 check whether it claims to have the same IP as registry
            if (IP.equals(requesterSocket.getInetAddress().getHostAddress()) || (requesterSocket.getInetAddress().getHostAddress().equals("127.0.0.1") && registryServerThread.getHostIP().equals(IP)))
            {
                // Honest, add to registrySenders and registeredNodes
                registrySenders.put(requesterSocket, sender);
                registeredNodes.put(full_identity, sender);

                // Then send register success
                sendRegisterResponse(sender, true, "Registration request successful. The number of messaging nodes currently constituting the overlay is " + String.valueOf(registeredNodes.size()) + ".");
            }
            else
            {
                // Not honest
                System.out.println(requesterSocket.getInetAddress().getHostAddress());
                sendRegisterResponse(sender,false,"IP mismatch.");
            }


        } catch (IOException ioe) {
            System.out.print("Failed to create TCP Sender instance.");
        }


    }

    private void sendRegisterResponse(TCPSender sender, boolean status, String addiinfo)
    {
        try {
            RegisterResponse registerResponse = new RegisterResponse(status, addiinfo);

            // Send marshalled bytes
            sender.sendData(registerResponse.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("Failed to send register response.");
        }

    }

    private void handleDeregisterRequest(Event e)
    {
        DeregisterRequest deregisterRequest = (DeregisterRequest)e;
        String IP = deregisterRequest.getIP();
        int port = deregisterRequest.getPort();
        Socket requesterSocket = deregisterRequest.getRequesterSocket();
        System.out.println("Got deregister request from IP: " + IP + " Port: " + String.valueOf(port) + ".");
        try {
            if (!registrySenders.containsKey(requesterSocket))
            {
                // Previously not registered, send failure message
                // First create TCPSender
                TCPSender sender = new TCPSender(requesterSocket);
                sendDeregisterResponse(sender, false, "Node was not previously registered.");
                return;
            }

            // requesterSocket contained in registrySenders, check full identity in registeredNodes
            String fullIdentity = IP + ":" + String.valueOf(port);
            if (!registeredNodes.containsKey(fullIdentity))
            {
                // Previously not registered, send failure message
                sendDeregisterResponse(registrySenders.get(requesterSocket), false, "Node was not previously registered.");
                return;
            }

            // Previously registered, check whether it is honest
            if (!(IP.equals(requesterSocket.getInetAddress().getHostAddress()) || (requesterSocket.getInetAddress().getHostAddress().equals("127.0.0.1") && registryServerThread.getHostIP().equals(IP))))
            {
                // Not honest, send failure message
                sendDeregisterResponse(registrySenders.get(requesterSocket), false, "Claimed false IP address.");
                return;
            }

            // Previously registered and honest, do deregistration
            TCPSender sender = registrySenders.get(requesterSocket);
            registrySenders.remove(requesterSocket);
            registeredNodes.remove(fullIdentity);
            sendDeregisterResponse(sender, true, "Deregistration request successful. The number of remaining messaging nodes is " + String.valueOf(registeredNodes.size()) + ".");
        }
        catch (IOException ioe)
        {
            System.out.println("Failed to respond to deregister request.");
        }
    }

    private void sendDeregisterResponse(TCPSender sender, boolean status, String addiinfo)
    {
        try {
            DeregisterResponse deregisterResponse = new DeregisterResponse(status, addiinfo);
            sender.sendData(deregisterResponse.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("Failed to send deregister response.");
        }
    }
}
