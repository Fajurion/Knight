package ac.knight.check.movement.velocity;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.util.MathUtil;

public class VelocityHorizontal extends Check {

    public VelocityHorizontal(UserData userData) {
        super("Velocity", "Horizontal", "Checks for horizontal velocity modification.", 12, userData);
    }

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {
            if(movement.velocityTicks == 1) {
                double veloX = movement.lastDeltaX + movement.remainingVeloX;
                double veloZ = movement.lastDeltaZ + movement.remainingVeloZ;

                if(movement.deltaX < movement.velocityX * 0.7 && movement.deltaZ < movement.velocityZ * 0.7)  {
                    if(this.increaseBuffer() > 3) {
                        this.fail();
                    }
                } else this.decreaseBuffer(0.05);
            }
        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new VelocityHorizontal(data);
    }
}
