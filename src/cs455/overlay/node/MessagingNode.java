package cs455.overlay.node;

import cs455.overlay.dijkstra.NodeAndLink;
import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.CommunicationTracker;
import cs455.overlay.wireformats.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MessagingNode implements Node {

    private String registry_host;
    private int registry_port;
    private TCPServerThread messagingNodeServerThread = null;
    private TCPSender registrySender = null;
    private TCPReceiverThread registryReceiver = null;
    private boolean shortestPathCalculated = false;
    private boolean sentDeregisterRequest = false;
    private int numNodes = 0;
    private String stringNodes = null;
    private int numLinks = 0;
    private String linkInfo = null;
    private RoutingCache routingCache = null;
    private CommunicationTracker communicationTracker = null;

    @Override
    public void onEvent(Event e) {
        Protocol type = e.getType();
        switch (type)
        {
            case REGISTER_RESPONSE:
                handleRegisterResponse(e);
                break;
            case DEREGISTER_RESPONSE:
                handleDeregisterResponse(e);
                break;
            case MESSAGING_NODES_LIST:
                handleMessagingNodesList(e);
                break;
            case LINK_WEIGHTS:
                handleLinkWeights(e);
                break;
            case HANDSHAKE:
                handleHandshake(e);
                break;
            case TASK_INITIATE:
                handleTaskInitiate(e);
                break;
            case MESSAGE:
                handleMessage(e);
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
            if (userinput.equals("print-shortest-path"))
            {
                // TODO: handle user input of "print-shortest-path"
            }
            else if (userinput.equals("exit-overlay"))
            {
                if (sentDeregisterRequest)
                {
                    System.out.println("You've already exited overlay before. Program should exit shortly.");
                }
                else
                {
                    sendDeregisterRequest();
                    System.out.println("Deregister request sent.");
                    sentDeregisterRequest = true;
                }
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
        // Connect to registry and create registrySender and receiver
        try {
            Socket registrySocket = new Socket(registry_host, registry_port);
            registrySender = new TCPSender(registrySocket);
            registryReceiver = new TCPReceiverThread(registrySocket, this);
            registryReceiver.start();
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
        if (!status)
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

    private void sendDeregisterRequest()
    {
        // Send deregister request to the registry
        DeregisterRequest deregisterRequest = new DeregisterRequest(messagingNodeServerThread.getHostIP(), messagingNodeServerThread.getPort());
        try {
            registrySender.sendData(deregisterRequest.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("Unable to send deregister request to registry. Program will now exit");
            System.exit(1);
        }
    }

    private void handleDeregisterResponse(Event e)
    {
        DeregisterResponse deregisterResponse = (DeregisterResponse)e;
        boolean status = deregisterResponse.getStatus();
        String addiinfo = deregisterResponse.getAddiinfo();
        if (!status)
        {
            // Deregister failed
            System.out.println("Received failure deregister response from registry with following information:");
            System.out.println(addiinfo);
            System.out.println("Program will now exit.");
            System.exit(1);
        }
        else
        {
            // Deregister Success
            System.out.println("Successfully registered with registry. The registry sent the following information:");
            System.out.println(addiinfo);
            System.out.println("Program will now exit.");
            System.exit(0);
        }
    }

    private void handleMessagingNodesList(Event e)
    {
        MessagingNodesList messagingNodesList = (MessagingNodesList)e;
        this.numNodes = messagingNodesList.getNumNodes();
        this.stringNodes = messagingNodesList.getStringNodes();
    }

    private void handleLinkWeights(Event e)
    {
        LinkWeights linkWeights = (LinkWeights)e;
        this.numLinks = linkWeights.getNumLinks();
        this.linkInfo = linkWeights.getLinkInfo();

        // Do Dijkstra
        String currentNode = messagingNodeServerThread.getHostIP() + ":" + String.valueOf(messagingNodeServerThread.getPort());
        NodeAndLink nodeAndLink = new NodeAndLink(this.numNodes, this.stringNodes, this.numLinks, this.linkInfo, currentNode, this);
        routingCache = nodeAndLink.Dijkstra();

        // Sleep 5 seconds before trying to start connection
        try {
            System.out.println("Dijkstra calculation complete, sleep five seconds before trying to reach out to neighbours");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e1) {
            System.out.println("Sleep interrupted.");
        }

        for (String node: routingCache.getNodesNeedToContact())
        {
            String[] splitted = node.split(":");
            String IP = splitted[0];
            int port = Integer.valueOf(splitted[1]);
            try {
                Socket senderSocket = new Socket(IP, port);

                // Spawn receiver thread and create sender class
                TCPReceiverThread receiverThread = new TCPReceiverThread(senderSocket, this);
                this.messagingNodeServerThread.addReceiver(receiverThread);

                routingCache.setSender(node, new TCPSender(senderSocket));
            } catch (IOException e1) {
                System.out.println("Failed to connect to neighbour. Program will now exit.");
                System.exit(1);
            }
        }
    }

    private void handleHandshake(Event e)
    {
        Handshake handshake = (Handshake)e;
        String IP = handshake.getIP();
        int port = handshake.getPort();
        Socket socket = handshake.getSocket();

        // Add IP:Port and socket pair to routing cache
        String identity = IP + ":" + String.valueOf(port);
        try {
            routingCache.setSender(identity, new TCPSender(socket));
        } catch (IOException e1) {
            System.out.println("Failed to set sender.");
        }
    }

    public void sendPreparationComplete()
    {
        PreparationComplete preparationComplete = new PreparationComplete(messagingNodeServerThread.getHostIP(), messagingNodeServerThread.getPort());
        try {
            registrySender.sendData(preparationComplete.getBytes());
        } catch (IOException e) {
            System.out.println("Failed to send preparation complete message. Program will now exit.");
            System.exit(1);
        }
    }

    private void handleTaskInitiate(Event e)
    {
        int numRounds = ((TaskInitiate)e).getNumRounds();
        //Initialize communication tracker
        communicationTracker = new CommunicationTracker();
        // Sleep for 3 seconds to let all communication tracker initialize
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e1) {
            System.out.println("Interrupted when waiting for all communication tracker to initialize");
        }

        ArrayList<String> otherNodes = routingCache.getOtherNodes();
        int numOtherNodes = otherNodes.size();
        Random random = new Random();

        for (int i = 0; i < numRounds; i++)
        {
            // First find a dest node
            String dest = otherNodes.get(random.nextInt(numOtherNodes));
            String src = messagingNodeServerThread.getHostIP() + ":" + String.valueOf(messagingNodeServerThread.getPort());

            // Next find next hop
            String nextHop = routingCache.getNextHop(dest);
            TCPSender nextHopSender = routingCache.getSender(nextHop);

            // Send five messages
            for (int j = 0; j < 5; j++)
            {
                int payload = random.nextInt();
                Message message = new Message(src, dest, payload);
                try {
                    byte[] data = message.getBytes();
                    nextHopSender.sendData(data);
                    communicationTracker.incrementSendTracker();
                    communicationTracker.addSendSummation(payload);
                }
                catch (IOException ioe)
                {
                    System.out.println("Failed to initiate a message to " + dest);
                }
            }
        }

        // Finished all rounds, send task complete to registry
        try {
            TaskComplete taskComplete = new TaskComplete(messagingNodeServerThread.getHostIP(), messagingNodeServerThread.getPort());
            byte[] data = taskComplete.getBytes();
            registrySender.sendData(data);
        }
        catch (IOException ioe)
        {
            System.out.println("Failed to send task complete message to registry.");
            System.exit(1);
        }
    }

    private void handleMessage(Event e)
    {
        Message message = (Message)e;
        String currentNode = messagingNodeServerThread.getHostIP() + ":" + String.valueOf(messagingNodeServerThread.getPort());
        String srcNode = message.getSrcIdentity();
        String destNode = message.getDestIdentity();
        int payload = message.getPayload();

        if (destNode.equals(currentNode))
        {
            // Reach destination
            communicationTracker.incrementReceiveTracker();
            communicationTracker.addReceiveSummation(payload);
        }
        else
        {
            // Relay
            communicationTracker.incrementRelayTracker();

            // Get nextHop
            String nextHop = routingCache.getNextHop(destNode);
            TCPSender nextHopSender = routingCache.getSender(nextHop);

            try {
                byte[] data = message.getBytes();
                nextHopSender.sendData(data);
            }
            catch (IOException ioe)
            {
                System.out.println("Error when trying to relaying message from " + srcNode + " to " + destNode);
            }
        }
    }

}
