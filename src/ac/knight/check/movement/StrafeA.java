package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.util.BlockUtil;

public class StrafeA extends Check {

    public StrafeA(UserData userData) {
        super("Strafe", "A", "Checks for modifiing motions mid-air.", 12, userData);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.velocityTicks < 6
                    || BlockUtil.isVehicleNearby(userData.user.getPlayer())
                    || userData.isFlying()
                    || userData.user.hitbox.containsSolidBlocks
                    || userData.teleportTicks <= 1
                    || userData.user.hitbox.stuck)
                return;

            if(userData.airTicks > 2) {
                double friction = 0.91f;

                double predX = userData.lastDeltaX * friction;
                double predZ = userData.lastDeltaZ * friction;

                double diffX = Math.abs(userData.deltaX - predX);
                double diffZ = Math.abs(userData.deltaZ - predZ);
                if((diffX > 0.026 || diffZ > 0.026) && userData.deltaXZ > 0.1) {
                    if(this.increaseBuffer(0.5) > 5) {
                        this.fail();
                    }
                } else {
                    this.decreaseBuffer(0.05);
                }

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new StrafeA(data);
    }
}
