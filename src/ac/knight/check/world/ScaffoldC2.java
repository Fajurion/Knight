package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.RotationProcessor;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class ScaffoldC2 extends Check {

    public ScaffoldC2(UserData userData) {
        super("Scaffold", "C2", "Checks for scaffold-like rotation pattern.", 12, userData);
    }

    private int streak = 0;

    private RotationProcessor rotation;

    @Override
    public void init(UserData data) {
        rotation = (RotationProcessor) data.processor(RotationProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if (e.getPacket() instanceof PacketPlayInBlockPlace) {
                PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) e.getPacket();

                if(packet.a().getX() != -1 && packet.a().getY() != -1) {

                    if(Math.abs(rotation.deltaPitch) == 0 && Math.abs(rotation.lastDeltaPitch) == 0 && Math.abs(rotation.lastLastDeltaPitch) == 0
                    && Math.abs(rotation.deltaYaw) > 5) {
                        if(streak++ > 5) {
                            if(this.increaseBuffer() > 3) {
                                this.fail("streak", streak + "");
                            }
                        }
                    } else {
                        this.decreaseBuffer(0.05);
                        streak = 0;
                    }

                }

            }
        }

    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldC2(data);
    }
}
