package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.util.MathUtil;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;

public class AimA1 extends Check {

    public AimA1(UserData userData) {
        super("Aim", "A1", "Checks for smooth rotations. (pitch)", 20, userData);
    }


    private double lastAtan2Pitch;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            final double atan2Pitch = Math.atan2(userData.deltaPitch, userData.lastDeltaPitch);

            if(MathUtil.round(atan2Pitch).equals(MathUtil.round(lastAtan2Pitch)) && userData.deltaPitch > 0.1f && userData.attackTicks < 3 && userData.deltaXZ > 0.1) {
                if(this.increaseBuffer() > 8)
                    this.fail();
            } else this.decreaseBuffer(0.05);

            lastAtan2Pitch = atan2Pitch;

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimA1(data);
    }
}
