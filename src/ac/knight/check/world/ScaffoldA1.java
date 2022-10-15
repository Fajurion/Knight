package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.impl.EventIncoming;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class ScaffoldA1 extends Check {

    public ScaffoldA1(UserData userData) {
        super("Scaffold", "A", "Checks for an invalid hitvector.", 4, userData);
    }

    @Override
    public void init(UserData data) {}

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if(e.getPacket() instanceof PacketPlayInBlockPlace) {

                PacketPlayInBlockPlace place = (PacketPlayInBlockPlace) e.getPacket();
                if((place.d() > 1f || place.e() > 1f || place.f() > 1f) && place.a().getY() != -1) {
                    if(this.increaseBuffer() > 0) {
                        this.fail();
                    }
                } else this.decreaseBuffer(0.001);

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldA1(data);
    }
}
