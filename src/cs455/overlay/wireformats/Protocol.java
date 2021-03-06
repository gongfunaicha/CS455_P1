package cs455.overlay.wireformats;

import java.util.HashMap;

// ENUM to Integer template from http://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum
public enum Protocol {
    REGISTER_REQUEST(1),
    REGISTER_RESPONSE(2),
    DEREGISTER_REQUEST(3),
    DEREGISTER_RESPONSE(4),
    MESSAGING_NODES_LIST(5),
    LINK_WEIGHTS(6),
    HANDSHAKE(7),
    PREPARATION_COMPLETE(8),
    TASK_INITIATE(9),
    MESSAGE(10),
    TASK_COMPLETE(11),
    PULL_TRAFFIC_SUMMARY(12),
    TRAFFIC_SUMMARY(13),
    CONNECTION_DROPPED(14);

    private int value;
    private static HashMap<Integer, Protocol> map = new HashMap<>();

    Protocol(int val)
    {
        this.value = val;
    }

    static
    {
        for (Protocol protocol: Protocol.values())
        {
            map.put(protocol.value, protocol);
        }
    }

    public static Protocol valueOf(int protocol)
    {
        return map.get(protocol);
    }

    public int getValue()
    {
        return value;
    }
}
