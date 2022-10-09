package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class ScaffoldB extends Check {

    public ScaffoldB(UserData userData) {
        super("Scaffold", "B", "Checks for invalid movement while scaffolding.", 7, userData);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if (e.getPacket() instanceof PacketPlayInBlockPlace) {
                PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) e.getPacket();

                if(packet.a().getX() != -1 && packet.a().getY() != -1) {

                    if(Math.abs(userData.deltaXZ - userData.lastDeltaXZ) == 0 && userData.deltaXZ > 0.2) {
                        if(this.increaseBuffer(1) > 3) {
                            this.fail();
                        }
                    } else this.decreaseBuffer(0.05);

                }

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldB(data);
    }
}
