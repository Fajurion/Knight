package ac.knight.event.impl;

import ac.knight.event.Event;
import net.minecraft.server.v1_8_R3.Packet;

public class EventIncoming extends Event {

    private final Packet packet;

    public EventIncoming(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
