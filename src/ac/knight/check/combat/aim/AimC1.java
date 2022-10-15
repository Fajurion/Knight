package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;

public class AimC1 extends Check {

    public AimC1(UserData userData) {
        super("Aim", "C1", "Checks for snappy rotations. (yaw)", 12, userData);
    }

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) userData.processor(MovementProcessor.class);
        rotation = (RotationProcessor) userData.processor(RotationProcessor.class);
    }

    private MovementProcessor movement;
    private RotationProcessor rotation;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            if(Math.abs(rotation.deltaYaw) < 2f && Math.abs(rotation.lastDeltaYaw) > 50f && Math.abs(rotation.lastLastDeltaYaw) < 2f && movement.teleportTicks > 1) {

                if(this.increaseBuffer() > 3) {
                    this.fail();
                }

            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimC1(data);
    }
}
