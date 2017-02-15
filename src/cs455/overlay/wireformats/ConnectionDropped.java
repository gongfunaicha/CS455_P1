package cs455.overlay.wireformats;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by bennywang on 2/15/17.
 */
public class ConnectionDropped implements Event{

    private Socket socket = null;

    public ConnectionDropped(Socket socket)
    {
        this.socket = socket;
    }

    public Socket getSocket()
    {
        return socket;
    }

    // Will not use
    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public Protocol getType() {
        return Protocol.CONNECTION_DROPPED;
    }
}
