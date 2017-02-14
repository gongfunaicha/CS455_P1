package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessagingNodesList implements Event{

    private int numNodes = 0;
    private String stringNodes = null;

    public MessagingNodesList(ArrayList<String> Nodes)
    {
        stringNodes = "";
        for (String Node: Nodes)
        {
            if (!stringNodes.equals(""))
            {
                // If not first node, add "\n" before adding next node
                stringNodes += "\n";
            }
            stringNodes += Node;
        }
        this.numNodes = Nodes.size();
    }

    public MessagingNodesList(int numNodes, String Nodes)
    {
        this.numNodes = numNodes;
        this.stringNodes = Nodes;
    }

    // Format: int Message_Type, int numNodes, int len_stringNodes, String stringNodes
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.MESSAGING_NODES_LIST.getValue());

        // Write numNodes
        dout.writeInt(numNodes);

        // Write len_stringNodes
        dout.writeInt(stringNodes.length());

        // Write stringNodes
        dout.write(stringNodes.getBytes());

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public Protocol getType() {
        return Protocol.MESSAGING_NODES_LIST;
    }

    public String getStringNodes()
    {
        return stringNodes;
    }

    public int getNumNodes()
    {
        return numNodes;
    }

}
