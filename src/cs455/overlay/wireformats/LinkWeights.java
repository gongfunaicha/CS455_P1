package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LinkWeights implements Event{

    private int numLinks = 0;
    private String linkInfo = null;

    public LinkWeights(int numLinks, String linkInfo)
    {
        this.numLinks = numLinks;
        this.linkInfo = linkInfo;
    }

    // Format: int Message_Type, int numLinks, int len_linkInfo, String linkInfo
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.LINK_WEIGHTS.getValue());

        // Write numLinks
        dout.writeInt(numLinks);

        // Write len_linkInfo
        dout.writeInt(linkInfo.length());

        // Write linkInfo
        dout.write(linkInfo.getBytes());

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public Protocol getType() {
        return Protocol.LINK_WEIGHTS;
    }

    public int getNumLinks()
    {
        return numLinks;
    }

    public String getLinkInfo()
    {
        return linkInfo;
    }
}
