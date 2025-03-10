package ac.knight.check.network;

import ac.knight.event.impl.EventOutgoing;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;

public class TimerA extends Check {

    public TimerA(UserData userData) {
        super("Timer", "A", "Checks for modified game speed.", 8, userData);
        setKicking(false);
    }

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }
    private long balance = 0, lastPacket = 0;
    private int tickCount = 0;

    @Override
    public void onEvent(Event event) {

        if(event instanceof EventOutgoing) {

            EventOutgoing e = (EventOutgoing) event;
            if(e.getPacket() instanceof PacketPlayOutPosition) {
                balance -= 50;
            }

        } else if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if(e.getPacket() instanceof PacketPlayInFlying) {

                final long delay = System.currentTimeMillis() - lastPacket;
                lastPacket = System.currentTimeMillis();

                if(delay > 1000000
                || movement.teleportTicks <= 5) {
                    return;
                }

                if(tickCount++ > 100) {
                    balance = 0;
                    tickCount = 0;
                }

                balance += 50 - delay;
                boolean lagging = delay <= 5;

                if((balance > 1000) && !lagging) {

                    if(this.increaseBuffer() > 2) {
                        this.fail("type", "high", "balance", balance + "", "delay", delay + "");
                    }
                    balance = 0;

                } else this.decreaseBuffer(0.001);

                if((balance < -20000) && !lagging) {

                    if(this.increaseBuffer() > 2) {
                        this.fail("type", "low", "balance", balance + "", "delay", delay + "");
                    }
                    balance = 0;

                }

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new TimerA(data);
    }
}
