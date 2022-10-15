package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.RotationProcessor;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class ScaffoldC4 extends Check {

    public ScaffoldC4(UserData userData) {
        super("Scaffold", "C4", "Checks for scaffold-like rotation pattern.", 12, userData);
    }

    private int streak = 0;
    private int pitchRotations = 0, yawRotations = 0;

    private RotationProcessor rotation;

    @Override
    public void init(UserData data) {
        rotation = (RotationProcessor) data.processor(RotationProcessor.class);
    }

    @Override
    public void onEvent(Event event) {

        if(event instanceof EventRotation) {
            if(Math.abs(rotation.deltaPitch) > 0) {
                pitchRotations++;
            } else if(Math.abs(rotation.deltaYaw) > 0) {
                yawRotations++;
            }
        } else if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if (e.getPacket() instanceof PacketPlayInBlockPlace) {
                PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) e.getPacket();

                if(packet.a().getX() != -1 && packet.a().getY() != -1) {

                    if(pitchRotations == 1 || yawRotations == 1) {
                        if(streak++ > 7) {
                            if(this.increaseBuffer(1) > 3) {
                                this.fail();
                            }
                        }
                    } else {
                        streak = 0;
                        this.decreaseBuffer(0.05);
                    }

                    pitchRotations = 0;
                    yawRotations = 0;
                }

            }
        }

    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldC4(data);
    }
}
