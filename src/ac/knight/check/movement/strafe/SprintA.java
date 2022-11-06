package ac.knight.check.movement.strafe;

import ac.knight.event.impl.EventMove;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.user.processor.impl.RotationProcessor;
import ac.knight.util.MathUtil;
import org.bukkit.util.Vector;

public class SprintA extends Check {

    public SprintA(UserData userData) {
        super("Sprint", "A", "Checks for an invalid sprint direction.", 12, userData);
        setKicking(false);
    }

    private MovementProcessor movement;
    private RotationProcessor rotation;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
        rotation = (RotationProcessor) data.processor(RotationProcessor.class);
    }
    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(movement.teleportTicks < 3
                    || userData.isFlying())
                return;

            Vector walkingDirection = new Vector(movement.deltaX, 0, movement.deltaZ);
            Vector direction = userData.getDirection(rotation.yaw, 0);

            double limit = 0.24 + Math.sqrt(Math.pow(movement.remainingVeloX, 2) + Math.pow(movement.remainingVeloZ, 2));
            if(!movement.onGround) {
                limit += 0.05;
            }

            if(movement.groundTicks < 30) {
                limit += 0.05;
            }

            limit = userData.getBaseSpeed((float) limit);

            if(walkingDirection.angle(direction) > 1.6 && movement.deltaXZ > limit) {
                if(this.increaseBuffer() > 3) {
                    this.fail("motionXZ", MathUtil.round(movement.deltaXZ) + "", "limit", MathUtil.round(limit) + "", "angle", MathUtil.round(walkingDirection.angle(direction)) + "");
                }
            } else this.decreaseBuffer(0.05);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SprintA(data);
    }
}
