package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.util.BlockUtil;
import org.bukkit.util.Vector;

public class SpeedB2 extends Check {

    public SpeedB2(UserData userData) {
        super("Speed", "B2", "Checks for horizontal acceleration/deceleration modification.", 12, userData);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if (userData.isFlying()
                    || userData.teleportTicks < 3
                    || userData.ticksExisted < 40
                    || userData.deltaXZ < 0.1
                    || BlockUtil.isVehicleNearby(userData.user.getPlayer())) return;

            double accel = Math.abs(userData.deltaXZ - userData.lastDeltaXZ);
            accel *= 1.0E10;

            if(userData.deltaYaw > 1.5f && accel < 1.0) {
                if(this.increaseBuffer() > 3) {
                    this.fail("deltaYaw", userData.deltaYaw + "", "accel", accel + "");
                }
            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SpeedB2(data);
    }

}
