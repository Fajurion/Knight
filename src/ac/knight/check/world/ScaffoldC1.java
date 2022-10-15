package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class ScaffoldC1 extends Check {

    public ScaffoldC1(UserData userData) {
        super("Scaffold", "C1", "Checks for scaffold-like rotation pattern.", 12, userData);
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

                    if(rotation.deltaPitch > 10 && rotation.lastDeltaPitch < -2 && rotation.lastLastDeltaPitch < -2) {
                        if(streak++ > 5) {
                            if(this.increaseBuffer(1) > 3) {
                                this.fail("streak", streak + "");
                            }
                        }
                    } else {
                        streak = 0;
                        this.decreaseBuffer(0.05);
                    }

                }

            }
        }

    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldC1(data);
    }
}
