package ac.knight.check.combat.killaura;

import ac.knight.check.Check;
import ac.knight.event.impl.EventMove;
import ac.knight.event.Event;
import ac.knight.user.UserData;

public class KillauraA extends Check {

    public KillauraA(UserData userData) {
        super("Killaura", "A", "Checks for keep sprint.", 5, userData);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.deltaXZ > 0) {

                final double accel = Math.abs(userData.deltaXZ - userData.lastDeltaXZ);

                if(accel < 0.002 && userData.attackTicks <= 1 && userData.user.getPlayer().isSprinting() && userData.deltaXZ > 0.22) {
                    if(this.increaseBuffer() > 2) {
                        this.fail("acceleration", accel + "");
                    }
                } else this.decreaseBuffer(0.05);

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new KillauraA(data);
    }
}
