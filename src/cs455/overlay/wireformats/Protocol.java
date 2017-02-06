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
    CALCULATION_COMPLETE(7),
    TASK_INITIATE(8),
    MESSAGE(9),
    TASK_COMPLETE(10),
    PULL_TRAFFIC_SUMMARY(11),
    TRAFFIC_SUMMARY(12);

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
