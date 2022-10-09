package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;

public class AimD2 extends Check {

    public AimD2(UserData userData) {
        super("Aim", "D2", "Checks for snappy rotations. (pitch)", 12, userData);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            if(Math.abs(userData.deltaPitch) < 2f && Math.abs(userData.lastDeltaPitch) > 20f && Math.abs(userData.lastLastDeltaPitch) < 2f && userData.teleportTicks > 1) {

                if(this.increaseBuffer() > 3) {
                    this.fail();
                }

            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimD2(data);
    }
}
