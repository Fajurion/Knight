package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.util.MathUtil;

public class AimA2 extends Check {

    public AimA2(UserData userData) {
        super("Aim", "A2", "Checks for smooth rotations. (yaw)", 20, userData);
    }


    private double lastAtan2Yaw;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            final double atan2Yaw = Math.atan2(userData.deltaYaw, userData.lastDeltaYaw);

            if(MathUtil.round(atan2Yaw).equals(MathUtil.round(lastAtan2Yaw)) && userData.deltaYaw > 0.1f && userData.attackTicks < 3 && userData.deltaXZ > 0.1) {
                if(this.increaseBuffer() > 8)
                    this.fail();
            } else this.decreaseBuffer(0.05);

            lastAtan2Yaw = atan2Yaw;

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimA2(data);
    }
}
