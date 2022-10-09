package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.util.GhostblockData;
import ac.knight.util.MovementUtil;
import ac.knight.user.UserData;

import java.util.ArrayList;

public class FlightB extends Check {

    public FlightB(UserData userData) {
        super("Flight", "B", "Checks for floating too long.", 12, userData);
    }

    private ArrayList<GhostblockData> ghostblocks = new ArrayList<>();

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.velocityTicks < 30
                    || userData.teleportTicks < 3
                    || userData.liquidTicks > 3)
                return;

            if(!userData.user.hitbox.touchesGround) {

            }
        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new FlightB(data);
    }
}
