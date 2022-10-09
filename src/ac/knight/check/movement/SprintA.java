package ac.knight.check.movement;

import ac.knight.event.impl.EventMove;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.util.MathUtil;
import org.bukkit.util.Vector;

public class SprintA extends Check {

    public SprintA(UserData userData) {
        super("Sprint", "A", "Checks for an invalid sprint direction.", 12, userData);
        setKicking(false);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.teleportTicks < 3
                    || userData.isFlying())
                return;

            Vector walkingDirection = new Vector(userData.deltaX, 0, userData.deltaZ);
            Vector direction = userData.getDirection(userData.yaw, 0);

            double limit = 0.24 + Math.sqrt(Math.pow(userData.remainingVeloX, 2) + Math.pow(userData.remainingVeloZ, 2));
            if(!userData.onGround) {
                limit += 0.05;
            }

            if(userData.groundTicks < 30) {
                limit += 0.05;
            }

            limit = userData.getBaseSpeed((float) limit);

            if(walkingDirection.angle(direction) > 1.6 && userData.deltaXZ > limit) {
                if(this.increaseBuffer() > 3) {
                    this.fail("motionXZ", MathUtil.round(userData.deltaXZ) + "", "limit", MathUtil.round(limit) + "", "angle", MathUtil.round(walkingDirection.angle(direction)) + "");
                }
            } else this.decreaseBuffer(0.05);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SprintA(data);
    }
}
