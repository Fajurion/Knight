package ac.knight.check.network;

import ac.knight.event.impl.EventOutgoing;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;

public class TimerA extends Check {

    public TimerA(UserData userData) {
        super("Timer", "A", "Checks for modified game speed.", 8, userData);
        setKicking(false);
    }

    private long balance = 0, lastPacket = 0, lastDelay = 0;
    private int tickCount = 0, lagStreak = 0, nonDecendStreak = 0;

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
                || userData.teleportTicks <= 5) {
                    return;
                }

                if(tickCount++ > 100) {
                    balance = 0;
                    tickCount = 0;
                }

                balance += 50 - delay;
                boolean lagging = delay <= 5;

                if(balance < -2000) {
                    balance = 0;
                }

                if(balance > 50 && !lagging) {
                    if(lagStreak++ > 50) {
                        if(this.increaseBuffer() > 2) {
                            this.fail("type", "streak", "balance", balance + "", "streak", lagStreak + "");
                            lagStreak = 0;
                            balance = 0;
                        }
                    }
                } else {
                    nonDecendStreak = 0;
                    lagStreak = 0;
                }

                if((balance > 2000) && !lagging) {

                    if(this.increaseBuffer() > 2) {
                        this.fail("type", "high", "balance", balance + "", "delay", delay + "");
                    }
                    balance = 0;

                } else this.decreaseBuffer(0.001);

                lastDelay = delay;

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new TimerA(data);
    }
}
