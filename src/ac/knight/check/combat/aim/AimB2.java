package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;

public class AimB2 extends Check {

    public AimB2(UserData userData) {
        super("Aim", "B2", "Checks for jitter rotations. (pitch)", 20, userData);
    }

    private int streak = 0;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            if(userData.teleportTicks < 3)
                return;

            final boolean invalid1 = userData.deltaPitch > 0 && userData.lastDeltaPitch < 0 && userData.lastLastDeltaPitch > 0;
            final boolean invalid2 = userData.deltaPitch < 0 && userData.lastDeltaPitch > 0 && userData.lastLastDeltaPitch < 0;

            if(invalid1 || invalid2) {
                if(streak++ > 3) {
                    if(this.increaseBuffer() > 3) {
                        this.fail("streak", streak + "");
                    }
                }
            } else {
                streak = 0;
                this.decreaseBuffer(0.05);
            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimB2(data);
    }
}
