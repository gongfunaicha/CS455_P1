package cs455.overlay.wireformats;

// Interface Event, all wire format protocols need to implement Event
public interface Event {
    Protocol getType();
    byte[] getBytes();
}
