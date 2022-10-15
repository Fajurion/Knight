package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;

public class AimB2 extends Check {

    public AimB2(UserData userData) {
        super("Aim", "B2", "Checks for jitter rotations. (pitch)", 20, userData);
    }

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) userData.processor(MovementProcessor.class);
        rotation = (RotationProcessor) userData.processor(RotationProcessor.class);
    }

    private MovementProcessor movement;
    private RotationProcessor rotation;

    private int streak = 0;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            if(movement.teleportTicks < 3)
                return;

            final boolean invalid1 = rotation.deltaPitch > 0 && rotation.lastDeltaPitch < 0 && rotation.lastLastDeltaPitch > 0;
            final boolean invalid2 = rotation.deltaPitch < 0 && rotation.lastDeltaPitch > 0 && rotation.lastLastDeltaPitch < 0;

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
