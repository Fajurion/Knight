package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;
import ac.knight.util.MathUtil;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;

public class AimA1 extends Check {

    public AimA1(UserData userData) {
        super("Aim", "A1", "Checks for smooth rotations. (pitch)", 20, userData);
    }

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) userData.processor(MovementProcessor.class);
        rotation = (RotationProcessor) userData.processor(RotationProcessor.class);
    }

    private MovementProcessor movement;
    private RotationProcessor rotation;

    private double lastAtan2Pitch;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            final double atan2Pitch = Math.atan2(rotation.deltaPitch, rotation.lastDeltaPitch);

            if(MathUtil.round(atan2Pitch).equals(MathUtil.round(lastAtan2Pitch)) && rotation.deltaPitch > 0.1f /* && rotation.attackTicks < 3 TODO: FIX */ && movement.deltaXZ > 0.1) {
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
