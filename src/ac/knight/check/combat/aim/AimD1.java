package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;

public class AimD1 extends Check {

    public AimD1(UserData userData) {
        super("Aim", "D1", "Checks for snappy rotations. (yaw)", 12, userData);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            if(Math.abs(userData.deltaYaw) < 2f && Math.abs(userData.lastDeltaYaw) > 50f && Math.abs(userData.lastLastDeltaYaw) < 2f && userData.teleportTicks > 1) {

                if(this.increaseBuffer() > 3) {
                    this.fail();
                }

            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimD1(data);
    }
}
