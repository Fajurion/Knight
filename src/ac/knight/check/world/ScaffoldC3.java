package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class ScaffoldC3 extends Check {

    public ScaffoldC3(UserData userData) {
        super("Scaffold", "C3", "Checks for scaffold-like rotation pattern.", 12, userData);
    }

    private int streak = 0;
    private int rotations = 0;

    @Override
    public void onEvent(Event event) {

        if(event instanceof EventRotation) {
            rotations++;
        } else if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if (e.getPacket() instanceof PacketPlayInBlockPlace) {
                PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) e.getPacket();

                if(packet.a().getX() != -1 && packet.a().getY() != -1) {

                    if(rotations == 1) {
                        if(streak++ > 7) {
                            if(this.increaseBuffer(1) > 3) {
                                this.fail();
                            }
                        }
                    } else {
                        streak = 0;
                        this.decreaseBuffer(0.05);
                    }

                    rotations = 0;
                }

            }
        }

    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldC3(data);
    }
}
