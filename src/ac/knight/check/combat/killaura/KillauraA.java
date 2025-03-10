package ac.knight.check.combat.killaura;

import ac.knight.check.Check;
import ac.knight.event.impl.EventMove;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;

public class KillauraA extends Check {

    public KillauraA(UserData userData) {
        super("Killaura", "A", "Checks for keep sprint.", 5, userData);
    }

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(movement.deltaXZ > 0) {

                final double accel = Math.abs(movement.deltaXZ - movement.lastDeltaXZ);

                if(accel < 0.002 && movement.attackTicks <= 1 && userData.user.getPlayer().isSprinting() && movement.deltaXZ > 0.22) {
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
