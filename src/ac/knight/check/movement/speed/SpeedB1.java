package ac.knight.check.movement.speed;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.InitializationProcessor;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;
import ac.knight.util.BlockUtil;
import org.bukkit.util.Vector;

public class SpeedB1 extends Check {

    public SpeedB1(UserData userData) {
        super("Speed", "B1", "Checks for horizontal acceleration/deceleration modification.", 12, userData);
    }

    private double lastAngle = 0;

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

            Vector walkingDirection = new Vector(movement.deltaX, 0, movement.deltaZ);
            Vector direction = userData.getDirection(rotation.yaw, 0);
            double angle = walkingDirection.angle(direction);
            double angleDiff = Math.abs(angle - lastAngle);

            if(angleDiff > 0.05 && accel < 1.0) {
                if(this.increaseBuffer() > 3) {
                    this.fail("angle", angle + "", "accel", accel + "");
                }
            } else this.decreaseBuffer(0.01);

            lastAngle = angle;

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SpeedB1(data);
    }

}
