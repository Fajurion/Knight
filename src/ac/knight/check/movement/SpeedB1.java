package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.util.BlockUtil;
import org.bukkit.util.Vector;

public class SpeedB1 extends Check {

    public SpeedB1(UserData userData) {
        super("Speed", "B1", "Checks for horizontal acceleration/deceleration modification.", 12, userData);
    }

    private double lastAngle = 0;

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

            Vector walkingDirection = new Vector(userData.deltaX, 0, userData.deltaZ);
            Vector direction = userData.getDirection(userData.yaw, 0);
            double angle = walkingDirection.angle(direction);
            double angleDiff = Math.abs(angle - lastAngle);

            if(angleDiff > 0.05 && accel < 1.0) {
                if(this.increaseBuffer() > 3) {
                    this.fail("angle", angle + "", "accel", accel + "");
                }
            } else this.decreaseBuffer(0.01);

            lastAngle = angle;

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SpeedB1(data);
    }

}
