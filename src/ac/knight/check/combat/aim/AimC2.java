package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;

public class AimC2 extends Check {

    public AimC2(UserData userData) {
        super("Aim", "C2", "Checks for snappy rotations. (pitch)", 12, userData);
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

            if(Math.abs(rotation.deltaPitch) < 2f && Math.abs(rotation.lastDeltaPitch) > 20f && Math.abs(rotation.lastLastDeltaPitch) < 2f && movement.teleportTicks > 1) {

                if(this.increaseBuffer() > 3) {
                    this.fail();
                }

            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimC2(data);
    }
}
