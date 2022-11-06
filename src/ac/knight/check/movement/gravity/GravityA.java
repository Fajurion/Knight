package ac.knight.check.movement.gravity;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.util.MathUtil;
import ac.knight.util.MovementUtil;
import org.bukkit.Material;

public class GravityA extends Check {

    public GravityA(UserData movement) {
        super("Gravity", "A", "Checks for modified gravity.", 12, movement);
    }

    private boolean prevSlime = false;

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.isFlying()
                    || movement.ticksSinceLiquid > 6
                    || movement.velocityTicks < 3
                    || movement.teleportTicks < 3
                    || userData.user.hitbox.climbable
                    || MovementUtil.isCollidedVertically(userData.world(), movement.x, movement.y, movement.z))
                return;

            double pred = (movement.lastDeltaY - 0.08f) * 0.9800000190734863D;
            if(userData.user.getProtocolVersion() > 47) {
                pred = (movement.lastDeltaY - 0.08f) * 0.98;
            }
            final boolean exclude = movement.deltaY == 0;
            if(movement.lastDeltaY <= 0 && MathUtil.isRoughly(movement.deltaY, 0.41999998688697815, 0.001)) {
                return;
            }

            boolean prevWasOnSlime = prevSlime;
            boolean wasOnSlime = userData.user.hitbox.touchesGround && userData.user.hitbox.slimeBlocks;
            prevSlime = wasOnSlime;

            if(Math.abs(pred) < 0.005) pred = 0;
            if(MathUtil.isRoughly(movement.lastDeltaY, -0.1552, 0.001)) pred = 0.40444491418477924;
            if(MathUtil.isRoughly(movement.lastDeltaY, 0.40444491418477924, 0.001)) pred = 0.33319999363422426;
            if(MathUtil.isRoughly(movement.lastDeltaY, -0.15233518685055003, 0.001)) pred = 0.395575898673286;
            if(MathUtil.isRoughly(movement.lastDeltaY, 0.395575898673286, 0.001)) pred = 0.33319999363422426;

            if(movement.deltaY > -0.1 && movement.deltaY < -0.06) {
                return;
            }
            if(Math.abs(pred - movement.deltaY) > (userData.user.getProtocolVersion() > 47 ? 0.01D : 0.001D) && movement.deltaY > -0.13 && !exclude && !movement.packetOnGround && !wasOnSlime && !prevWasOnSlime) {
                if(userData.user.getProtocolVersion() > 47 && movement.iceTicks < 3) {
                    return;
                }
                if(movement.verticalTicks < 10 && movement.deltaY < 0.2) {
                    return;
                }
                boolean safeMode = false;
                if(!userData.user.getPlayer().getLocation().clone().add(0, -0.15, 0).getBlock().getType().equals(Material.AIR)) {
                    pred = userData.user.getPlayer().getLocation().clone().add(0, -0.15, 0).getBlock().getY() - movement.lastY + 1;
                    safeMode = true;
                    if(MathUtil.isRoughly(pred, movement.deltaY, 0.001)) {
                        return;
                    }
                }
                if(this.increaseBuffer(safeMode ? 0.5 : 1) > 1) {
                    System.out.println(Math.abs(pred - movement.deltaY) + " | " + pred + " | " + movement.deltaY + " | " + movement.lastDeltaY);
                    this.fail("motionY", MathUtil.round(movement.deltaY) + "", "prediction", MathUtil.round(pred) + "", "difference", MathUtil.round(Math.abs(pred - movement.deltaY)) + "");
                }
            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new GravityA(data);
    }
}
