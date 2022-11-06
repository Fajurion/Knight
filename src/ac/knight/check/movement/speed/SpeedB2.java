package ac.knight.check.movement.speed;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.InitializationProcessor;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;
import ac.knight.util.BlockUtil;

public class SpeedB2 extends Check {

    public SpeedB2(UserData userData) {
        super("Speed", "B2", "Checks for horizontal acceleration/deceleration modification.", 12, userData);
    }

    private MovementProcessor movement;
    private InitializationProcessor initialization;
    private RotationProcessor rotation;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
        initialization = (InitializationProcessor) data.processor(InitializationProcessor.class);
        rotation = (RotationProcessor) data.processor(RotationProcessor.class);
    }
    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if (userData.isFlying()
                    || movement.teleportTicks < 3
                    || initialization.ticksExisted < 40
                    || movement.deltaXZ < 0.1
                    || BlockUtil.isVehicleNearby(userData.user.getPlayer())) return;

            double accel = Math.abs(movement.deltaXZ - movement.lastDeltaXZ);
            accel *= 1.0E10;

            if(rotation.deltaYaw > 1.5f && accel < 1.0) {
                if(this.increaseBuffer() > 3) {
                    this.fail("deltaYaw", rotation.deltaYaw + "", "accel", accel + "");
                }
            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SpeedB2(data);
    }

}
