package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
    private Socket senderSocket = null;
    private DataOutputStream dout;

    public TCPSender(Socket socket) throws IOException {
        this.senderSocket = socket;
        dout = new DataOutputStream(senderSocket.getOutputStream());
    }

    public void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length;
        dout.writeInt(dataLength);
        dout.write(dataToSend, 0, dataLength);
        dout.flush();
    }
}
