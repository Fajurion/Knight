package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;
import ac.knight.util.MathUtil;

public class AimA2 extends Check {

    public AimA2(UserData userData) {
        super("Aim", "A2", "Checks for smooth rotations. (yaw)", 20, userData);
    }

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) userData.processor(MovementProcessor.class);
        rotation = (RotationProcessor) userData.processor(RotationProcessor.class);
    }

    private MovementProcessor movement;
    private RotationProcessor rotation;
    private double lastAtan2Yaw;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {
            final double atan2Yaw = Math.atan2(rotation.deltaYaw, rotation.lastDeltaYaw);

            if(MathUtil.round(atan2Yaw).equals(MathUtil.round(lastAtan2Yaw)) && rotation.deltaYaw > 0.1f /* && rotation.attackTicks < 3 TODO: FIX */ && movement.deltaXZ > 0.1) {
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
