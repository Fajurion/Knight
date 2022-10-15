package ac.knight.check.network;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;

public class SilentA extends Check {

    public SilentA(UserData userData) {
        super("Silent", "A", "Checks for invalid hotbar switching.", 10, userData);
    }

    private int lastSlot = -1;

    @Override
    public void init(UserData data) {}

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventIncoming) {
            EventIncoming e = (EventIncoming) event;
            if(e.getPacket() instanceof PacketPlayInHeldItemSlot) {
                PacketPlayInHeldItemSlot packet = (PacketPlayInHeldItemSlot) e.getPacket();

                if(packet.a() == lastSlot) {
                    if(this.increaseBuffer() > 0) {
                        this.fail();
                    }
                }

                lastSlot = packet.a();
            }
        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SilentA(data);
    }
}
