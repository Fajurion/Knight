package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.impl.EventMove;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import org.bukkit.Material;

public class FlightA extends Check {

    public FlightA(UserData userData) {
        super("Flight", "A", "Checks for similar motions mid-air.", 12, userData);
        setKicking(false);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.velocityTicks < 3
                    || userData.teleportTicks < 3
                    || userData.liquidTicks > 3
                    || userData.isFlying()
                    || userData.user.hitbox.climbable
                    || userData.user.hitbox.slimeBlocks
                    || userData.user.hitbox.containsSolidBlocks)
                return;

            if(!userData.packetOnGround && !userData.user.getPlayer().isDead() && Math.abs(userData.deltaY - userData.lastDeltaY) < 0.01 && userData.deltaY > -3) {
                if(userData.user.getProtocolVersion() > 47 && userData.iceTicks < 10) {
                    return;
                }
                if(this.increaseBuffer() > 3) {
                    this.fail();
                }
            } else this.decreaseBuffer(0.05);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new FlightA(data);
    }
}
