package cs455.overlay.node;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Registry implements Node {

    private TCPServerThread registryServerThread = null;
    private HashMap<Socket, TCPSender> registrySenders = null;
    private HashMap<String, TCPSender> registeredNodes = null;
    private OverlayCreator overlayCreator = null;
    private ArrayList<TCPSender> registeredSendersCache = null;
    private StatisticsCollectorAndDisplay statisticsCollectorAndDisplay = null;
    private final Object registerDeregisterLock = new Object();
    int numPreparedNodes = 0;
    int numCompletedNodes = 0;

    public Registry(int portnum)
    {
        // Start registry server thread
        startRegistryServerThread(portnum);

        registrySenders = new HashMap<>();
        registeredNodes = new HashMap<>();

        // Create buffered reader
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            String userinput;
            try {
                userinput = br.readLine();
            }
            catch (IOException ioe)
            {
                System.out.println("Failed to read user input.");
                continue;
            }
            if (userinput.equals("list-messaging-nodes"))
            {
                if (registeredNodes.size() != 0)
                {
                    System.out.println("The following messaging nodes are currently connected:");
                    for (String identity: registeredNodes.keySet())
                    {
                        System.out.println(identity);
                    }
                }
                else
                {
                    System.out.println("There is no messaging node currently connected.");
                }
            }
            else if (userinput.equals("list-weights"))
            {
                if (overlayCreator == null)
                {
                    System.out.println("Haven't setup overlay. Cannot list weights.");
                    continue;
                }
                System.out.println(overlayCreator.formattedOverlay());
            }
            else if (userinput.startsWith("setup-overlay "))
            {
                // Parse user input
                String numberOfConnections = userinput.substring(14);
                int numConn = 0;
                try {
                    numConn = Integer.valueOf(numberOfConnections);
                }
                catch (NumberFormatException nfe)
                {
                    System.out.println("Invalid number of connections");
                    continue;
                }
                if (numConn != 4)
                {
                    System.out.println("Currently only support 4 connections.");
                    continue;
                }
                if (registeredNodes.size() < 10)
                {
                    System.out.println("Setup overlay requires at least 10 messaging nodes.");
                    continue;
                }
                // Clear prepared nodes
                numPreparedNodes = 0;

                // Start creating overlay
                Set<String> Nodes = registeredNodes.keySet();
                registeredSendersCache = new ArrayList<>(registeredNodes.values());
                overlayCreator = new OverlayCreator(Nodes);
                overlayCreator.createOverlay();

                // Start to send of list of nodes need to connect
                for (String Node: Nodes)
                {
                    ArrayList<String> nodesNeedToConnect = overlayCreator.getNeedToConnect(Node);
                    try {
                        MessagingNodesList messagingNodesList = new MessagingNodesList(nodesNeedToConnect);
                        registeredNodes.get(Node).sendData(messagingNodesList.getBytes());
                    }
                    catch (IOException ioe)
                    {
                        System.out.println("Failed to send out messaging node list.");
                        System.exit(1);
                    }
                }

            }
            else if (userinput.equals("send-overlay-link-weights"))
            {
                if (overlayCreator == null)
                {
                    System.out.println("Could not send out overlay link weights information. Please run setup-overlay command first.");
                }
                else
                {
                    try {
                        // Reset prepared nodes
                        numPreparedNodes = 0;
                        LinkWeights linkWeights = new LinkWeights(overlayCreator.getNumLinks(), overlayCreator.formattedOverlay());
                        byte[] data = linkWeights.getBytes();
                        // Send data using all tcpSenders
                        for (TCPSender sender: registeredSendersCache)
                            sender.sendData(data);
                    }
                    catch (IOException ioe)
                    {
                        System.out.println("Failed to send out link weight.");
                    }
                }
            }
            else if (userinput.startsWith("start "))
            {
                if (overlayCreator == null)
                {
                    System.out.println("Haven't created overlay. Cannot start.");
                    continue;
                }
                if (numPreparedNodes != overlayCreator.getNumNodes())
                {
                    // Not all nodes are prepared
                    System.out.println("Not all nodes are currently prepared. Not able to issue start command.");
                    continue;
                }


                // get number of rounds
                String strNum = userinput.substring(6);
                int numRounds = 0;
                try {
                    numRounds = Integer.valueOf(strNum);
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Number of rounds must be an integer.");
                    continue;
                }

                // Clear completed node counter
                numCompletedNodes = 0;

                TaskInitiate taskInitiate = new TaskInitiate(numRounds);

                try {
                    byte[] data = taskInitiate.getBytes();

                    for (TCPSender sender: registeredSendersCache)
                    {
                        sender.sendData(data);
                    }
                    System.out.println("Task initiate command sent successfully.");
                }
                catch (IOException ioe)
                {
                    System.out.println("Failed to send out task initiate command.");
                    System.exit(1);
                }

            }
            else if (userinput.equals("exit"))
            {
                System.out.println("Registry will now exit.");
                System.exit(0);
            }
            else
            {
                System.out.println("Unrecognized command.");
            }
        }
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
            case PREPARATION_COMPLETE:
                handlePreparationComplete(e);
                break;
            case TASK_COMPLETE:
                handleTaskComplete(e);
                break;
            case TRAFFIC_SUMMARY:
                handleTrafficSummary(e);
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

        synchronized (registerDeregisterLock)
        {
            // Duplicate entry, send failure response
            if (registrySenders.containsKey(requesterSocket))
            {
                try {
                    sendRegisterResponse(registrySenders.get(requesterSocket),false,"Node had previously registered.");
                }
                catch (IOException e1)
                {
                    System.out.println("Link broken before registration response is sent.");
                }
                return;
            }

            try {
                String full_identity = IP + ":" + port;
                // First check whether it has registered
                if (registeredNodes.containsKey(full_identity))
                {
                    try {
                        sendRegisterResponse(registeredNodes.get(full_identity),false,"Node had previously registered.");
                    }
                    catch (IOException e1)
                    {
                        System.out.println("Link broken before registration response is sent.");
                    }
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
                    try {
                        sendRegisterResponse(sender, true, "Registration request successful. The number of messaging nodes currently constituting the overlay is " + String.valueOf(registeredNodes.size()) + ".");
                    }
                    catch (IOException e1)
                    {
                        System.out.println("Link broken before registration response is sent. Removing the messaging node from list.");
                        registrySenders.remove(requesterSocket);
                        registeredNodes.remove(full_identity);

                    }
                }
                else
                {
                    // Not honest
                    System.out.println(requesterSocket.getInetAddress().getHostAddress());
                    try {
                        sendRegisterResponse(sender,false,"IP mismatch.");
                    }
                    catch (IOException e1)
                    {
                        System.out.println("Link broken before registration response is sent.");
                    }
                }


            } catch (IOException ioe) {
                System.out.print("Failed to create TCP Sender instance.");
            }
        }

    }

    private void sendRegisterResponse(TCPSender sender, boolean status, String addiinfo) throws IOException
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

        synchronized (registerDeregisterLock)
        {
            if (overlayCreator != null)
            {
                System.out.println("Overlay was reset due to deregistration of a messaging node.");
                overlayCreator = null;
            }

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

    private synchronized void handlePreparationComplete(Event e)
    {
        numPreparedNodes++;
        if (numPreparedNodes == overlayCreator.getNumNodes())
        {
            // All nodes are prepared
            System.out.println("Received prepared information from all nodes.");
        }
    }

    private synchronized void handleTaskComplete(Event e)
    {
        numCompletedNodes++;
        if (numCompletedNodes == overlayCreator.getNumNodes())
        {
            // All nodes complete
            try {
                System.out.println("Received task complete message from all nodes.");
                System.out.println("30 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(10);
                System.out.println("20 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(10);
                System.out.println("10 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("5 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("4 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("3 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("2 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("1 seconds before issuing PULL_TRAFFIC_SUMMARY command.");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("Issuing PULL_TRAFFIC_SUMMARY command...");
            }
            catch (InterruptedException ie)
            {
                System.out.println("Interrupted when waiting to issue PULL_TRAFFIC_SUMMARY command.");
            }

            try {
                // Initialize statisticsCollectorAndDisplay
                statisticsCollectorAndDisplay = new StatisticsCollectorAndDisplay(overlayCreator.getNumNodes());

                PullTrafficSummary pullTrafficSummary = new PullTrafficSummary();
                byte[] data = pullTrafficSummary.getBytes();
                for (TCPSender sender: registeredSendersCache)
                {
                    sender.sendData(data);
                }
            }
            catch (IOException ioe)
            {
                System.out.println("Failed to send PULL_TRAFFIC_SUMMARY command.");
                System.exit(1);
            }
        }
    }

    private void handleTrafficSummary(Event e)
    {
        TrafficSummary trafficSummary = (TrafficSummary)e;
        statisticsCollectorAndDisplay.addTrafficSummary(trafficSummary);
    }
}
