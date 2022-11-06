package ac.knight.check.movement.gravity;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.util.GhostblockData;
import ac.knight.user.UserData;

import java.util.ArrayList;

public class FlightB extends Check {

    public FlightB(UserData userData) {
        super("Flight", "B", "Checks for floating too long.", 12, userData);
    }

    private ArrayList<GhostblockData> ghostblocks = new ArrayList<>();

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(movement.velocityTicks < 30
                    || movement.teleportTicks < 3
                    || movement.liquidTicks > 3)
                return;

            if(!userData.user.hitbox.touchesGround) {
                // TODO: Implement
            }
        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new FlightB(data);
    }
}
