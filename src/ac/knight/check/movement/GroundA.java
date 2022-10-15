package ac.knight.check.movement;

import ac.knight.event.impl.EventIncoming;
import ac.knight.event.impl.EventMove;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;

public class GroundA extends Check {

    public GroundA(UserData movement) {
        super("Ground", "A", "Checks for an invalid onground statement.", 12, movement);
    }

    private Location lastGroundLocation;
    private boolean packetGround, lastGround = true;
    private int placeTicks = 0;

    @Override
    public void init(UserData data) {
    }
    
    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.user.hitbox.safeGround) {
                lastGroundLocation = userData.user.getPlayer().getLocation();
            }

            placeTicks++;
            if(packetGround && placeTicks > 20 && !userData.user.hitbox.safeGround && !lastGround) {
                userData.user.getPlayer().teleport(lastGroundLocation);
            }

            lastGround = userData.user.hitbox.safeGround;

        } else if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if (e.getPacket() instanceof PacketPlayInFlying) {
                packetGround = ((PacketPlayInFlying) e.getPacket()).f();
            } else if(e.getPacket() instanceof PacketPlayInBlockPlace) {
                PacketPlayInBlockPlace place = (PacketPlayInBlockPlace) e.getPacket();
                if(place.a().getY() != -1) {
                    placeTicks = 0;
                }
            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new GroundA(data);
    }
}
