package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeregisterResponse implements Event{
    private boolean status = false;
    private String addiinfo = null;

    public DeregisterResponse(boolean status, String addiinfo)
    {
        this.status = status;
        this.addiinfo = addiinfo;
    }

    @Override
    public Protocol getType() {
        return Protocol.DEREGISTER_RESPONSE;
    }

    // Format: int Message_Type, byte status, int len_addiinfo, String addi_info
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        // Write Message Type
        dout.writeInt(Protocol.DEREGISTER_RESPONSE.getValue());

        // Write status
        dout.writeBoolean(status);

        // Write length of additional info
        dout.writeInt(addiinfo.length());

        // Write additional info
        dout.write(addiinfo.getBytes());

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public boolean getStatus()
    {
        return status;
    }

    public String getAddiinfo()
    {
        return addiinfo;
    }
}
