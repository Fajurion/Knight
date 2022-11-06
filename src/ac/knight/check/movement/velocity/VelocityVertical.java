package ac.knight.check.movement.velocity;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;

public class VelocityVertical extends Check {

    public VelocityVertical(UserData userData) {
        super("Velocity", "Vertical", "Checks for vertical velocity modification.", 12, userData);
    }

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(movement.velocityTicks < 5) {
                double veloXZ = movement.lastDeltaXZ + Math.hypot(movement.remainingVeloX, movement.remainingVeloZ);

                System.out.println(movement.deltaXZ + " | " + veloXZ);
            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new VelocityVertical(data);
    }
}
