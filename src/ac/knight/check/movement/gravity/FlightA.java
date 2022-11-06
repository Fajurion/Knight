package ac.knight.check.movement.gravity;

import ac.knight.check.Check;
import ac.knight.event.impl.EventMove;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;

public class FlightA extends Check {

    public FlightA(UserData userData) {
        super("Flight", "A", "Checks for similar motions mid-air.", 12, userData);
        setKicking(false);
    }

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(movement.velocityTicks < 3
                    || movement.teleportTicks < 3
                    || movement.liquidTicks > 3
                    || userData.isFlying()
                    || userData.user.hitbox.climbable
                    || userData.user.hitbox.slimeBlocks
                    || userData.user.hitbox.containsSolidBlocks)
                return;

            if(!movement.packetOnGround && !userData.user.getPlayer().isDead() && Math.abs(movement.deltaY - movement.lastDeltaY) < 0.01 && movement.deltaY > -3) {
                if(userData.user.getProtocolVersion() > 47 && movement.iceTicks < 10) {
                    return;
                }
                if(this.increaseBuffer() > 3) {
                    this.fail();
                }
            } else this.decreaseBuffer(0.05);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new FlightA(data);
    }
}
