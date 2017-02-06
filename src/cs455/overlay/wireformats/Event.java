package cs455.overlay.wireformats;

import java.io.IOException;

// Interface Event, all wire format protocols need to implement Event
public interface Event {
    Protocol getType();
    byte[] getBytes() throws IOException;
}
