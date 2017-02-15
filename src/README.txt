CS455 Homework 1 - Programming Component
Using Dijkstra Shortest Paths to Route Packets in a Network Overlay

Compilation:
Please use "make" command to compile the whole project.
If you want to remove the .class files produced by the compilation, please use command "make clean".

Executing project:
Registry:
	java cs455.overlay.node.Registry <portnum>
Messaging Node:
	java cs455.overlay.node.MessagingNode <registry-host> <registry-port>

Commands supported:
Registry:
	list-messaging-nodes
	list-weights
	setup-overlay <number-of-connections>
	send-overlay-link-weights
	start <number-of-rounds>
Messaging Node:
	print-shortest-path
	exit-overlay

Note:
When outputting traffic summary, "Node 1", "Node 2", etc are replaced by the actual IP address and port number of the node for clarity.
Please wait at least five seconds before issuing "send-overlay-link-weights" command to allow the messaging nodes to contact each other.

Files:
cs455/
	overlay/
		dijkstra/
			NodeAndLink.java: NodeAndLink is used to store nodes and links, and do Dijkstra.
			RoutingCache.java: A file that is used to store cache of routing table. Contains nextHop, text_version_route and other information.
		node/
			MessagingNode.java: Handles the main activity of messaging nodes, main method resides in this file.
			Node.java: Interface that contains onEvent method.
			Registry.java: Handles the main activity of registry, main method resides in this file.
		transport/
			TCPReceiverThread.java: A thread that handles receiving message from a socket.
			TCPSender.java: A class that handles sending bytes through a socket.
			TCPServerThread.java: A thread that handles incoming connections on a server socket.
		util/
			CommunicationTracker.java: Tracks the communication count on messaging node side.
			Link.java: Data structure of link.
			OverlayCreator.java: Help registry create desired overlay.
			StatisticsCollectorAndDisplay.java: Process and display communication count gathered from messaging nodes.
		wireformats/
			ConnectionDropped.java: Connection dropped event for registry.
			DeregisterRequest.java: Wire format for deregister request message.
			DeregisterResponse.java: Wire format for deregister response message.
			Event.java: Interface including method getType() and getBytes().
			EventFactory.java: A class that creates wire format event from bytes.
			Handshake.java: Wire format for handshake message (between messaging nodes).
			LinkWeights.java: Wire format for link weight message.
			Message.java: Wire format for actual message with payload (between messaging nodes).
			MessagingNodesList.java: Wire format for peer messaging nodes list.
			PreparationComplete.java: Wire format for preparation complete message.
			Protocol.java: Enum class that maps event to integer (and integer to event) for easier transmission.
			PullTrafficSummary.java: Wire format for pull traffic summary command.
			RegisterRequest.java: Wire format for register request message.
			RegisterResponse.java: Wire format for register response message.
			TaskComplete.java: Wire format for task complete message.
			TaskInitiate.java: Wire format for task initiate command.
			TrafficSummary.java: Wire format for traffic summary message.
			
			
Chen Wang
2/14/2017